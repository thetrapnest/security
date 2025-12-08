package ru.thetrapnest.security.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val showError: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        if (username == "student" && password == "security123") {
            _uiState.value = AuthUiState(isAuthenticated = true, showError = false)
        } else {
            _uiState.value = AuthUiState(isAuthenticated = false, showError = true)
        }
    }

    fun resetError() {
        _uiState.value = _uiState.value.copy(showError = false)
    }
}
