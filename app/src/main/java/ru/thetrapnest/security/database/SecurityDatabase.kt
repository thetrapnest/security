package ru.thetrapnest.security.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        VulnerabilityEntity::class,
        UserProgressEntity::class,
        UserEntity::class,
        UserAchievementEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(VulnerabilityTypeConverter::class)
abstract class SecurityDatabase : RoomDatabase() {
    abstract fun vulnerabilityDao(): VulnerabilityDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun userDao(): UserDao
    abstract fun userAchievementDao(): UserAchievementDao

    companion object {
        @Volatile
        private var INSTANCE: SecurityDatabase? = null

        fun getDatabase(context: Context): SecurityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SecurityDatabase::class.java,
                    "security_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
