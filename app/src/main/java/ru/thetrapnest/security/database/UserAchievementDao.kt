package ru.thetrapnest.security.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAchievementDao {
    @Query("SELECT * FROM user_achievements WHERE userId = :userId")
    fun getAchievementsForUser(userId: Long): Flow<List<UserAchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievement(achievement: UserAchievementEntity)
}
