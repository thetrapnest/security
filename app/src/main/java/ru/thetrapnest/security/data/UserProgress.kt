package ru.thetrapnest.security.data

data class UserProgress(
    val vulnerabilityId: Int,
    val completed: Boolean = false,
    val hintsUsed: Int = 0,
    val attempts: Int = 0
)