package ru.thetrapnest.security.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.thetrapnest.security.R
import ru.thetrapnest.security.utils.AchievementManager
import ru.thetrapnest.security.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: SecurityViewModel = viewModel()
) {
    val context = LocalContext.current
    val achievementManager = remember { AchievementManager.getInstance(context) }
    val progressList by viewModel.userProgress.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()
    val totalScenarios = 2 // We have 2 default scenarios
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Progress card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.your_progress),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        progress = if (totalScenarios > 0) completedCount.toFloat() / totalScenarios else 0f,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.scenarios_completed, completedCount, totalScenarios),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Achievements
            Text(
                text = stringResource(R.string.achievements),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Update achievements based on progress
            LaunchedEffect(completedCount, progressList) {
                if (completedCount >= 1 && !achievementManager.isFirstStepAchieved()) {
                    achievementManager.setFirstStepAchieved()
                }
                
                if (completedCount >= 2 && !achievementManager.isSecurityExpertAchieved()) {
                    achievementManager.setSecurityExpertAchieved()
                }
                
                if (progressList.any { it.hintsUsed == 0 && it.completed } &&
                    !achievementManager.isNoHintsNeededAchieved()) {
                    achievementManager.setNoHintsNeededAchieved()
                }
                
                if (progressList.any { it.attempts == 1 && it.completed } &&
                    !achievementManager.isFirstTryAchieved()) {
                    achievementManager.setFirstTryAchieved()
                }
            }
            
            if (achievementManager.isFirstStepAchieved()) {
                AchievementItem(
                    title = stringResource(R.string.first_step_title),
                    description = stringResource(R.string.first_step_desc),
                    unlocked = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                AchievementItem(
                    title = stringResource(R.string.first_step_title),
                    description = stringResource(R.string.first_step_desc),
                    unlocked = false
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (achievementManager.isSecurityExpertAchieved()) {
                AchievementItem(
                    title = stringResource(R.string.security_expert_title),
                    description = stringResource(R.string.security_expert_desc),
                    unlocked = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                AchievementItem(
                    title = stringResource(R.string.security_expert_title),
                    description = stringResource(R.string.security_expert_desc),
                    unlocked = false
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (achievementManager.isNoHintsNeededAchieved()) {
                AchievementItem(
                    title = stringResource(R.string.no_hints_needed_title),
                    description = stringResource(R.string.no_hints_needed_desc),
                    unlocked = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                AchievementItem(
                    title = stringResource(R.string.no_hints_needed_title),
                    description = stringResource(R.string.no_hints_needed_desc),
                    unlocked = false
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (achievementManager.isFirstTryAchieved()) {
                AchievementItem(
                    title = stringResource(R.string.first_try_title),
                    description = stringResource(R.string.first_try_desc),
                    unlocked = true
                )
            } else {
                AchievementItem(
                    title = stringResource(R.string.first_try_title),
                    description = stringResource(R.string.first_try_desc),
                    unlocked = false
                )
            }
        }
    }
}

@Composable
fun AchievementItem(
    title: String,
    description: String,
    unlocked: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (unlocked)
                    Icons.Default.Star
                else
                    Icons.Default.Lock,
                contentDescription = if (unlocked) stringResource(R.string.unlocked) else stringResource(R.string.locked),
                tint = if (unlocked)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (unlocked)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (unlocked)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}