package ru.thetrapnest.security.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val displayName: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val createdAt: Long,
    val lastLoginAt: Long
)
