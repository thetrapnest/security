package ru.thetrapnest.security.session

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentUserId = MutableStateFlow(
        preferences.getLong(KEY_CURRENT_USER_ID, NO_USER_ID).takeIf { it != NO_USER_ID }
    )
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    fun setCurrentUser(userId: Long) {
        preferences.edit().putLong(KEY_CURRENT_USER_ID, userId).apply()
        _currentUserId.value = userId
    }

    fun clearSession() {
        preferences.edit().remove(KEY_CURRENT_USER_ID).apply()
        _currentUserId.value = null
    }

    private companion object {
        const val PREFS_NAME = "security_session"
        const val KEY_CURRENT_USER_ID = "current_user_id"
        const val NO_USER_ID = -1L
    }
}
