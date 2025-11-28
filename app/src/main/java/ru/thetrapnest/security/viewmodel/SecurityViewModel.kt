package ru.thetrapnest.security.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.thetrapnest.security.data.Vulnerability
import ru.thetrapnest.security.data.UserProgress
import ru.thetrapnest.security.database.SecurityRepository
import ru.thetrapnest.security.database.VulnerabilityEntity
import ru.thetrapnest.security.database.UserProgressEntity

class SecurityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SecurityRepository(application)
    
    // Vulnerabilities
    private val _vulnerabilities = MutableStateFlow<List<VulnerabilityEntity>>(emptyList())
    val vulnerabilities: StateFlow<List<VulnerabilityEntity>> = _vulnerabilities.asStateFlow()
    
    // User progress
    private val _userProgress = MutableStateFlow<List<UserProgressEntity>>(emptyList())
    val userProgress: StateFlow<List<UserProgressEntity>> = _userProgress.asStateFlow()
    
    // Completed count
    private val _completedCount = MutableStateFlow(0)
    val completedCount: StateFlow<Int> = _completedCount.asStateFlow()
    
    init {
        viewModelScope.launch {
            repository.initializeDefaultData()
        }
        
        viewModelScope.launch {
            repository.getAllVulnerabilities().collect { vulnerabilities ->
                _vulnerabilities.value = vulnerabilities
            }
        }
        
        viewModelScope.launch {
            repository.getAllProgress().collect { progress ->
                _userProgress.value = progress
                _completedCount.value = progress.count { it.completed }
            }
        }
    }
    
    fun getVulnerabilityById(id: Int): Flow<VulnerabilityEntity?> {
        return repository.getVulnerabilityById(id)
    }
    
    fun getProgressByVulnerabilityId(vulnerabilityId: Int): UserProgressEntity? {
        return _userProgress.value.find { it.vulnerabilityId == vulnerabilityId }
    }
    
    fun saveProgress(progress: UserProgressEntity) {
        viewModelScope.launch {
            repository.insertProgress(progress)
        }
    }
    
    fun incrementAttempts(vulnerabilityId: Int) {
        viewModelScope.launch {
            val progress = getProgressByVulnerabilityId(vulnerabilityId) ?: UserProgressEntity(
                vulnerabilityId = vulnerabilityId,
                attempts = 0,
                hintsUsed = 0,
                completed = false
            )
            val updatedProgress = progress.copy(attempts = progress.attempts + 1)
            repository.insertProgress(updatedProgress)
        }
    }
    
    fun incrementHintsUsed(vulnerabilityId: Int) {
        viewModelScope.launch {
            val progress = getProgressByVulnerabilityId(vulnerabilityId) ?: UserProgressEntity(
                vulnerabilityId = vulnerabilityId,
                attempts = 0,
                hintsUsed = 0,
                completed = false
            )
            val updatedProgress = progress.copy(hintsUsed = progress.hintsUsed + 1)
            repository.insertProgress(updatedProgress)
        }
    }
    
    fun markAsCompleted(vulnerabilityId: Int) {
        viewModelScope.launch {
            val progress = getProgressByVulnerabilityId(vulnerabilityId) ?: UserProgressEntity(
                vulnerabilityId = vulnerabilityId,
                attempts = 0,
                hintsUsed = 0,
                completed = false
            )
            val updatedProgress = progress.copy(completed = true)
            repository.insertProgress(updatedProgress)
        }
    }
}