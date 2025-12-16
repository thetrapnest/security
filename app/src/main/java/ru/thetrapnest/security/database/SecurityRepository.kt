package ru.thetrapnest.security.database

import android.content.Context
import kotlinx.coroutines.flow.Flow
import ru.thetrapnest.security.data.Vulnerability
import ru.thetrapnest.security.data.UserProgress
import ru.thetrapnest.security.data.VulnerabilityType

class SecurityRepository(context: Context) {
    private val database = SecurityDatabase.getDatabase(context)
    private val vulnerabilityDao = database.vulnerabilityDao()
    private val userProgressDao = database.userProgressDao()

    fun getAllVulnerabilities(): Flow<List<VulnerabilityEntity>> {
        return vulnerabilityDao.getAllVulnerabilities()
    }

    fun getVulnerabilityById(id: Int): Flow<VulnerabilityEntity?> {
        return vulnerabilityDao.getVulnerabilityById(id)
    }

    fun getAllProgress(): Flow<List<UserProgressEntity>> {
        return userProgressDao.getAllProgress()
    }

    suspend fun getProgressByVulnerabilityId(vulnerabilityId: Int): UserProgressEntity? {
        return userProgressDao.getProgressByVulnerabilityId(vulnerabilityId)
    }

    suspend fun insertVulnerabilities(vulnerabilities: List<VulnerabilityEntity>) {
        vulnerabilityDao.insertVulnerabilities(vulnerabilities)
    }

    suspend fun insertProgress(progress: UserProgressEntity) {
        userProgressDao.insertProgress(progress)
    }

    suspend fun updateProgress(progress: UserProgressEntity) {
        userProgressDao.updateProgress(progress)
    }

    suspend fun getCompletedCount(): Int {
        return userProgressDao.getCompletedCount()
    }

    suspend fun initializeDefaultData() {
        // Check if we already have data
        val existingVulnerabilities = vulnerabilityDao.getVulnerabilitiesCount()
        if (existingVulnerabilities == 0) {
            // Insert default vulnerabilities
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
                )
            )
            vulnerabilityDao.insertVulnerabilities(defaultVulnerabilities)
        }
    }
}