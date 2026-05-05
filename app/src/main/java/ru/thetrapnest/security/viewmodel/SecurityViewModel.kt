package ru.thetrapnest.security.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.thetrapnest.security.R
import ru.thetrapnest.security.data.AchievementDefinition
import ru.thetrapnest.security.data.AchievementUiModel
import ru.thetrapnest.security.data.UserStats
import ru.thetrapnest.security.database.AuthResult
import ru.thetrapnest.security.database.SecurityRepository
import ru.thetrapnest.security.database.UserAchievementEntity
import ru.thetrapnest.security.database.UserEntity
import ru.thetrapnest.security.database.UserProgressEntity
import ru.thetrapnest.security.database.VulnerabilityEntity

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SecurityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SecurityRepository(application)

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _vulnerabilities = MutableStateFlow<List<VulnerabilityEntity>>(emptyList())
    val vulnerabilities: StateFlow<List<VulnerabilityEntity>> = _vulnerabilities.asStateFlow()

    private val _userProgress = MutableStateFlow<List<UserProgressEntity>>(emptyList())
    val userProgress: StateFlow<List<UserProgressEntity>> = _userProgress.asStateFlow()

    private val _completedCount = MutableStateFlow(0)
    val completedCount: StateFlow<Int> = _completedCount.asStateFlow()

    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats.asStateFlow()

    private val _achievements = MutableStateFlow<List<AchievementUiModel>>(emptyList())
    val achievements: StateFlow<List<AchievementUiModel>> = _achievements.asStateFlow()

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultData()
        }

        viewModelScope.launch {
            repository.currentUser.collectLatest { user ->
                _currentUser.value = user
            }
        }

        viewModelScope.launch {
            repository.getAllVulnerabilities().collectLatest { vulnerabilities ->
                _vulnerabilities.value = vulnerabilities
            }
        }

        viewModelScope.launch {
            repository.getAllProgress().collectLatest { progress ->
                _userProgress.value = progress
                _completedCount.value = progress.count { it.completed }
            }
        }

        viewModelScope.launch {
            combine(
                repository.getAllAchievements(),
                currentUser,
                vulnerabilities,
                userProgress
            ) { unlockedAchievements, user, vulnerabilities, progress ->
                val stats = UserStats(
                    totalScenarios = vulnerabilities.size,
                    completedCount = progress.count { it.completed },
                    totalAttempts = progress.sumOf { it.attempts },
                    totalHintsUsed = progress.sumOf { it.hintsUsed },
                    hintFreeCompletions = progress.count { it.completed && it.hintsUsed == 0 },
                    firstTryCompletions = progress.count { it.completed && it.attempts == 1 }
                )
                val models = buildAchievements(stats, unlockedAchievements)
                AchievementSnapshot(user = user, stats = stats, models = models)
            }.collectLatest { snapshot ->
                _userStats.value = snapshot.stats
                _achievements.value = snapshot.models
                val userId = snapshot.user?.id
                if (userId != null) {
                    unlockAchievements(userId, snapshot.models)
                }
            }
        }
    }

    fun getVulnerabilityById(id: Int): Flow<VulnerabilityEntity?> {
        return repository.getVulnerabilityById(id)
    }

    fun getProgressByVulnerabilityId(vulnerabilityId: Int): UserProgressEntity? {
        return _userProgress.value.find { it.vulnerabilityId == vulnerabilityId }
    }

    fun login(email: String, password: String) {
        submitAuth {
            repository.login(email = email, password = password)
        }
    }

    fun register(displayName: String, email: String, password: String) {
        submitAuth {
            repository.register(displayName = displayName, email = email, password = password)
        }
    }

    fun logout() {
        repository.logout()
        _authUiState.value = AuthUiState()
    }

    fun clearAuthError() {
        _authUiState.value = _authUiState.value.copy(errorMessage = null)
    }

    fun incrementAttempts(vulnerabilityId: Int) {
        viewModelScope.launch {
            repository.incrementAttempts(vulnerabilityId)
        }
    }

    fun incrementHintsUsed(vulnerabilityId: Int) {
        viewModelScope.launch {
            repository.incrementHintsUsed(vulnerabilityId)
        }
    }

    fun markAsCompleted(vulnerabilityId: Int) {
        viewModelScope.launch {
            repository.markAsCompleted(vulnerabilityId)
        }
    }

    private fun submitAuth(action: suspend () -> AuthResult) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState(isLoading = true)
            _authUiState.value = when (val result = action()) {
                is AuthResult.Success -> AuthUiState()
                is AuthResult.Error -> AuthUiState(errorMessage = result.message)
            }
        }
    }

    private fun buildAchievements(
        stats: UserStats,
        unlockedAchievements: List<UserAchievementEntity>
    ): List<AchievementUiModel> {
        val unlockedMap = unlockedAchievements.associate { it.achievementId to it.unlockedAt }

        return achievementDefinitions().map { definition ->
            val goal = definition.goalProvider(stats).coerceAtLeast(1)
            val progress = definition.progressProvider(stats).coerceAtLeast(0)
            val unlockedAt = unlockedMap[definition.id]
            AchievementUiModel(
                id = definition.id,
                title = definition.title,
                description = definition.description,
                progress = progress.coerceAtMost(goal),
                goal = goal,
                unlocked = unlockedAt != null || progress >= goal,
                unlockedAt = unlockedAt
            )
        }
    }

    private suspend fun unlockAchievements(userId: Long, achievements: List<AchievementUiModel>) {
        achievements
            .filter { it.unlocked && it.unlockedAt == null }
            .forEach { achievement ->
                repository.insertAchievement(
                    UserAchievementEntity(
                        userId = userId,
                        achievementId = achievement.id,
                        unlockedAt = System.currentTimeMillis()
                    )
                )
            }
    }

    private fun achievementDefinitions(): List<AchievementDefinition> {
        val resources = getApplication<Application>().resources
        return listOf(
            AchievementDefinition(
                id = "first_step",
                title = resources.getString(R.string.first_step_title),
                description = resources.getString(R.string.first_step_desc),
                progressProvider = { it.completedCount },
                goalProvider = { 1 }
            ),
            AchievementDefinition(
                id = "pathfinder",
                title = resources.getString(R.string.pathfinder_title),
                description = resources.getString(R.string.pathfinder_desc),
                progressProvider = { it.completedCount },
                goalProvider = { minOf(it.totalScenarios.coerceAtLeast(1), 2) }
            ),
            AchievementDefinition(
                id = "security_expert",
                title = resources.getString(R.string.security_expert_title),
                description = resources.getString(R.string.security_expert_desc),
                progressProvider = { it.completedCount },
                goalProvider = { it.totalScenarios.coerceAtLeast(1) }
            ),
            AchievementDefinition(
                id = "no_hints_needed",
                title = resources.getString(R.string.no_hints_needed_title),
                description = resources.getString(R.string.no_hints_needed_desc),
                progressProvider = { it.hintFreeCompletions },
                goalProvider = { 1 }
            ),
            AchievementDefinition(
                id = "first_try",
                title = resources.getString(R.string.first_try_title),
                description = resources.getString(R.string.first_try_desc),
                progressProvider = { it.firstTryCompletions },
                goalProvider = { 1 }
            ),
            AchievementDefinition(
                id = "clean_series",
                title = resources.getString(R.string.clean_series_title),
                description = resources.getString(R.string.clean_series_desc),
                progressProvider = { it.hintFreeCompletions },
                goalProvider = { minOf(it.totalScenarios.coerceAtLeast(1), 2) }
            ),
            AchievementDefinition(
                id = "persistent_researcher",
                title = resources.getString(R.string.persistent_researcher_title),
                description = resources.getString(R.string.persistent_researcher_desc),
                progressProvider = { it.totalAttempts },
                goalProvider = { 10 }
            )
        )
    }

    private data class AchievementSnapshot(
        val user: UserEntity?,
        val stats: UserStats,
        val models: List<AchievementUiModel>
    )
}
