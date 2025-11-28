package ru.thetrapnest.security.utils

import android.content.Context
import android.content.SharedPreferences

class AchievementManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("achievements", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var INSTANCE: AchievementManager? = null

        fun getInstance(context: Context): AchievementManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AchievementManager(context).also { INSTANCE = it }
            }
        }
    }

    // Achievement keys
    private object Keys {
        const val FIRST_STEP = "first_step"
        const val SECURITY_EXPERT = "security_expert"
        const val NO_HINTS_NEEDED = "no_hints_needed"
        const val FIRST_TRY = "first_try"
    }

    // Set achievements
    fun setFirstStepAchieved() {
        sharedPreferences.edit().putBoolean(Keys.FIRST_STEP, true).apply()
    }

    fun setSecurityExpertAchieved() {
        sharedPreferences.edit().putBoolean(Keys.SECURITY_EXPERT, true).apply()
    }

    fun setNoHintsNeededAchieved() {
        sharedPreferences.edit().putBoolean(Keys.NO_HINTS_NEEDED, true).apply()
    }

    fun setFirstTryAchieved() {
        sharedPreferences.edit().putBoolean(Keys.FIRST_TRY, true).apply()
    }

    // Check achievements
    fun isFirstStepAchieved(): Boolean {
        return sharedPreferences.getBoolean(Keys.FIRST_STEP, false)
    }

    fun isSecurityExpertAchieved(): Boolean {
        return sharedPreferences.getBoolean(Keys.SECURITY_EXPERT, false)
    }

    fun isNoHintsNeededAchieved(): Boolean {
        return sharedPreferences.getBoolean(Keys.NO_HINTS_NEEDED, false)
    }

    fun isFirstTryAchieved(): Boolean {
        return sharedPreferences.getBoolean(Keys.FIRST_TRY, false)
    }

    // Reset all achievements (for testing purposes)
    fun resetAllAchievements() {
        sharedPreferences.edit()
            .remove(Keys.FIRST_STEP)
            .remove(Keys.SECURITY_EXPERT)
            .remove(Keys.NO_HINTS_NEEDED)
            .remove(Keys.FIRST_TRY)
            .apply()
    }
}