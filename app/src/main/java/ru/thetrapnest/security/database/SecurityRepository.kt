package ru.thetrapnest.security.database

import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import ru.thetrapnest.security.data.VulnerabilityType
import ru.thetrapnest.security.session.SessionManager
import ru.thetrapnest.security.utils.PasswordHasher

sealed interface AuthResult {
    data class Success(val user: UserEntity) : AuthResult
    data class Error(val message: String) : AuthResult
}

@OptIn(ExperimentalCoroutinesApi::class)
class SecurityRepository(context: Context) {
    private val database = SecurityDatabase.getDatabase(context)
    private val vulnerabilityDao = database.vulnerabilityDao()
    private val userProgressDao = database.userProgressDao()
    private val userDao = database.userDao()
    private val userAchievementDao = database.userAchievementDao()
    private val sessionManager = SessionManager(context)

    val currentUserId: StateFlow<Long?> = sessionManager.currentUserId

    val currentUser: Flow<UserEntity?> = currentUserId.flatMapLatest { userId ->
        if (userId == null) {
            flowOf(null)
        } else {
            userDao.observeUserById(userId)
        }
    }

    fun getAllVulnerabilities(): Flow<List<VulnerabilityEntity>> {
        return vulnerabilityDao.getAllVulnerabilities()
    }

    fun getVulnerabilityById(id: Int): Flow<VulnerabilityEntity?> {
        return vulnerabilityDao.getVulnerabilityById(id)
    }

    fun getAllProgress(): Flow<List<UserProgressEntity>> {
        return currentUserId.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                userProgressDao.getAllProgress(userId)
            }
        }
    }

    fun getAllAchievements(): Flow<List<UserAchievementEntity>> {
        return currentUserId.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                userAchievementDao.getAchievementsForUser(userId)
            }
        }
    }

    suspend fun getProgressByVulnerabilityId(vulnerabilityId: Int): UserProgressEntity? {
        val userId = currentUserId.value ?: return null
        return userProgressDao.getProgressByVulnerabilityId(userId, vulnerabilityId)
    }

    suspend fun insertVulnerabilities(vulnerabilities: List<VulnerabilityEntity>) {
        vulnerabilityDao.insertVulnerabilities(vulnerabilities)
    }

    suspend fun insertProgress(progress: UserProgressEntity) {
        userProgressDao.insertProgress(progress)
    }

    suspend fun insertAchievement(achievement: UserAchievementEntity) {
        userAchievementDao.insertAchievement(achievement)
    }

    suspend fun updateProgress(progress: UserProgressEntity) {
        userProgressDao.updateProgress(progress)
    }

    suspend fun getCompletedCount(): Int {
        val userId = currentUserId.value ?: return 0
        return userProgressDao.getCompletedCount(userId)
    }

    suspend fun register(displayName: String, email: String, password: String): AuthResult {
        val normalizedName = displayName.trim()
        val normalizedEmail = email.trim().lowercase()
        val normalizedPassword = password.trim()

        if (normalizedName.length < 2) {
            return AuthResult.Error("Имя должно содержать минимум 2 символа")
        }
        if (normalizedEmail.isBlank() || !normalizedEmail.contains("@")) {
            return AuthResult.Error("Укажите корректный email")
        }
        if (normalizedPassword.length < 6) {
            return AuthResult.Error("Пароль должен содержать минимум 6 символов")
        }
        if (userDao.getUserByEmail(normalizedEmail) != null) {
            return AuthResult.Error("Пользователь с таким email уже существует")
        }

        val salt = PasswordHasher.generateSalt()
        val now = System.currentTimeMillis()
        val userId = userDao.insertUser(
            UserEntity(
                displayName = normalizedName,
                email = normalizedEmail,
                passwordHash = PasswordHasher.hashPassword(normalizedPassword, salt),
                passwordSalt = salt,
                createdAt = now,
                lastLoginAt = now
            )
        )
        sessionManager.setCurrentUser(userId)
        return AuthResult.Success(userDao.getUserById(userId) ?: return AuthResult.Error("Не удалось создать пользователя"))
    }

    suspend fun login(email: String, password: String): AuthResult {
        val normalizedEmail = email.trim().lowercase()
        val normalizedPassword = password.trim()

        if (normalizedEmail.isBlank() || normalizedPassword.isBlank()) {
            return AuthResult.Error("Заполните email и пароль")
        }

        val user = userDao.getUserByEmail(normalizedEmail)
            ?: return AuthResult.Error("Пользователь не найден")

        if (!PasswordHasher.matches(normalizedPassword, user.passwordSalt, user.passwordHash)) {
            return AuthResult.Error("Неверный пароль")
        }

        val now = System.currentTimeMillis()
        userDao.updateLastLoginAt(user.id, now)
        sessionManager.setCurrentUser(user.id)
        return AuthResult.Success(user.copy(lastLoginAt = now))
    }

    fun logout() {
        sessionManager.clearSession()
    }

    suspend fun incrementAttempts(vulnerabilityId: Int) {
        val userId = currentUserId.value ?: return
        val currentProgress = userProgressDao.getProgressByVulnerabilityId(userId, vulnerabilityId)
        val updatedProgress = (currentProgress ?: UserProgressEntity(
            userId = userId,
            vulnerabilityId = vulnerabilityId
        )).copy(
            attempts = (currentProgress?.attempts ?: 0) + 1,
            lastPlayedAt = System.currentTimeMillis()
        )
        userProgressDao.insertProgress(updatedProgress)
    }

    suspend fun incrementHintsUsed(vulnerabilityId: Int) {
        val userId = currentUserId.value ?: return
        val currentProgress = userProgressDao.getProgressByVulnerabilityId(userId, vulnerabilityId)
        val updatedProgress = (currentProgress ?: UserProgressEntity(
            userId = userId,
            vulnerabilityId = vulnerabilityId
        )).copy(
            hintsUsed = (currentProgress?.hintsUsed ?: 0) + 1,
            lastPlayedAt = System.currentTimeMillis()
        )
        userProgressDao.insertProgress(updatedProgress)
    }

    suspend fun markAsCompleted(vulnerabilityId: Int) {
        val userId = currentUserId.value ?: return
        val currentProgress = userProgressDao.getProgressByVulnerabilityId(userId, vulnerabilityId)
        val updatedProgress = (currentProgress ?: UserProgressEntity(
            userId = userId,
            vulnerabilityId = vulnerabilityId
        )).copy(
            completed = true,
            completedAt = currentProgress?.completedAt ?: System.currentTimeMillis(),
            lastPlayedAt = System.currentTimeMillis()
        )
        userProgressDao.insertProgress(updatedProgress)
    }

    suspend fun initializeDefaultData() {
        val defaultVulnerabilities = listOf(
            VulnerabilityEntity(
                id = 1,
                title = "Межсайтовый скриптинг (XSS)",
                description = "Уязвимость, позволяющая злоумышленникам внедрять вредоносные скрипты в веб-страницы, которые просматривают другие пользователи.",
                type = VulnerabilityType.XSS,
                theory = "XSS возникает, когда веб-приложение вставляет непроверенные данные на страницу без валидации или экранирования. Это позволяет злоумышленнику выполнять произвольные скрипты в браузере жертвы.",
                example = "Пример: <script>alert('XSS-атака!')</script>",
                hint = "Попробуйте вставить тег <script> в поле ввода."
            ),
            VulnerabilityEntity(
                id = 2,
                title = "SQL-инъекция (SQLi)",
                description = "Техника внедрения кода, использующая уязвимости в обработке запросов: вредоносные SQL-выражения вставляются через поле ввода.",
                type = VulnerabilityType.SQLI,
                theory = "SQL-инъекция возникает, когда ввод пользователя включается в SQL-запрос без очистки и параметризации. Так злоумышленник может изменить структуру запроса и получить доступ к данным.",
                example = "Пример: ' OR '1'='1",
                hint = "Попробуйте ввести условие, которое всегда истинно."
            ),
            VulnerabilityEntity(
                id = 3,
                title = "LDAP-инъекция",
                description = "Атака на сервисы каталогов, при которой злоумышленник меняет LDAP-фильтр через небезопасно вставленный пользовательский ввод.",
                type = VulnerabilityType.LDAPI,
                theory = "LDAP-инъекция возникает, когда приложение собирает LDAP-запрос строковой конкатенацией. Подставляя специальные символы и операторы фильтра, атакующий может обойти проверку пользователя или расширить поиск.",
                example = "Пример: *)(|(uid=*))",
                hint = "Попробуйте закрыть текущий фильтр и добавить условие с подстановочным символом *."
            ),
            VulnerabilityEntity(
                id = 4,
                title = "XPath-инъекция",
                description = "Уязвимость в приложениях, формирующих XPath-запросы из пользовательского ввода без экранирования и проверки.",
                type = VulnerabilityType.XPATHI,
                theory = "XPath-инъекция похожа на SQLi, но нацелена на XML-документы и XPath-выражения. Если пользовательский ввод вставляется прямо в XPath-запрос, можно изменить его логику и получить доступ к лишним данным.",
                example = "Пример: ' or '1'='1",
                hint = "Попробуйте добавить условие, которое сделает XPath-предикат истинным для любой записи."
            )
        )

        val existingVulnerabilities = vulnerabilityDao.getVulnerabilitiesCount()
        if (existingVulnerabilities < defaultVulnerabilities.size) {
            vulnerabilityDao.insertVulnerabilities(defaultVulnerabilities)
        }
    }
}
