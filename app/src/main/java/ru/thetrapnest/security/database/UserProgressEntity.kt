package ru.thetrapnest.security.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val vulnerabilityId: Int,
    val completed: Boolean = false,
    val hintsUsed: Int = 0,
    val attempts: Int = 0
)