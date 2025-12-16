package ru.thetrapnest.security.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
@Suppress("UnusedParameter")
fun ProfileScreen(
    navController: NavController,
    viewModel: SecurityViewModel = viewModel()
) {
    val context = LocalContext.current
    val achievementManager = remember { AchievementManager.getInstance(context) }
    val progressList by viewModel.userProgress.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()
    val totalScenarios = 2
    val hintsUsed = progressList.sumOf { it.hintsUsed }
    val attempts = progressList.sumOf { it.attempts }

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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.your_progress),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    CircularProgressIndicator(
                        progress = if (totalScenarios > 0) completedCount.toFloat() / totalScenarios else 0f,
                        modifier = Modifier.size(120.dp),
                        strokeWidth = 10.dp
                    )
                    Text(
                        text = stringResource(R.string.scenarios_completed, completedCount, totalScenarios),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.attempt_hint_counter, attempts, hintsUsed),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            NextGoalSection(completedCount, totalScenarios, hintsUsed)

            Text(
                text = stringResource(R.string.achievements),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

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

            AchievementItem(
                title = stringResource(R.string.first_step_title),
                description = stringResource(R.string.first_step_desc),
                unlocked = achievementManager.isFirstStepAchieved()
            )
            AchievementSpacer()
            AchievementItem(
                title = stringResource(R.string.security_expert_title),
                description = stringResource(R.string.security_expert_desc),
                unlocked = achievementManager.isSecurityExpertAchieved()
            )
            AchievementSpacer()
            AchievementItem(
                title = stringResource(R.string.no_hints_needed_title),
                description = stringResource(R.string.no_hints_needed_desc),
                unlocked = achievementManager.isNoHintsNeededAchieved()
            )
            AchievementSpacer()
            AchievementItem(
                title = stringResource(R.string.first_try_title),
                description = stringResource(R.string.first_try_desc),
                unlocked = achievementManager.isFirstTryAchieved()
            )
        }
    }
}

@Composable
private fun NextGoalSection(completedCount: Int, totalScenarios: Int, hintsUsed: Int) {
    val nextGoalText = when {
        completedCount < totalScenarios -> stringResource(R.string.next_goal_complete, completedCount + 1)
        hintsUsed > 0 -> stringResource(R.string.next_goal_no_hints)
        else -> stringResource(R.string.next_goal_master)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = stringResource(R.string.next_goal), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = nextGoalText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
private fun AchievementSpacer() {
    Spacer(modifier = Modifier.height(8.dp))
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
