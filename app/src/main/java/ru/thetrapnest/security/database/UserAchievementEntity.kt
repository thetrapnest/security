package ru.thetrapnest.security.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "user_achievements",
    primaryKeys = ["userId", "achievementId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class UserAchievementEntity(
    val userId: Long,
    val achievementId: String,
    val unlockedAt: Long
)
