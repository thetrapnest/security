package ru.thetrapnest.security.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    fun getAllProgress(userId: Long): Flow<List<UserProgressEntity>>

    @Query("SELECT * FROM user_progress WHERE userId = :userId AND vulnerabilityId = :vulnerabilityId")
    suspend fun getProgressByVulnerabilityId(userId: Long, vulnerabilityId: Int): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserProgressEntity)

    @Update
    suspend fun updateProgress(progress: UserProgressEntity)

    @Query("SELECT COUNT(*) FROM user_progress WHERE userId = :userId AND completed = 1")
    suspend fun getCompletedCount(userId: Long): Int
}
