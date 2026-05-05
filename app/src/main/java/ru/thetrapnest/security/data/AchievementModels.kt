package ru.thetrapnest.security.data

data class UserStats(
    val totalScenarios: Int = 0,
    val completedCount: Int = 0,
    val totalAttempts: Int = 0,
    val totalHintsUsed: Int = 0,
    val hintFreeCompletions: Int = 0,
    val firstTryCompletions: Int = 0
)

data class AchievementDefinition(
    val id: String,
    val title: String,
    val description: String,
    val progressProvider: (UserStats) -> Int,
    val goalProvider: (UserStats) -> Int
)

data class AchievementUiModel(
    val id: String,
    val title: String,
    val description: String,
    val progress: Int,
    val goal: Int,
    val unlocked: Boolean,
    val unlockedAt: Long?
)
