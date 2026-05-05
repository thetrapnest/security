package ru.thetrapnest.security.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.thetrapnest.security.R
import ru.thetrapnest.security.data.AchievementUiModel
import ru.thetrapnest.security.database.UserEntity
import ru.thetrapnest.security.ui.components.SecurityBackground
import ru.thetrapnest.security.ui.components.SecurityBottomNav
import ru.thetrapnest.security.ui.components.SecurityGlassCard
import ru.thetrapnest.security.ui.components.SecurityMetricPill
import ru.thetrapnest.security.ui.components.SecuritySectionHeading
import ru.thetrapnest.security.ui.components.SecurityTag
import ru.thetrapnest.security.ui.theme.Aqua
import ru.thetrapnest.security.ui.theme.Mint
import ru.thetrapnest.security.ui.theme.Sun
import ru.thetrapnest.security.ui.theme.Violet
import ru.thetrapnest.security.viewmodel.SecurityViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: SecurityViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val stats by viewModel.userStats.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val completionRatio = if (stats.totalScenarios > 0) {
        stats.completedCount.toFloat() / stats.totalScenarios.toFloat()
    } else {
        0f
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        bottomBar = {
            SecurityBottomNav(
                selectedRoute = "profile",
                onScenariosClick = { navController.navigate("scenarios") },
                onProfileClick = {}
            )
        }
    ) { paddingValues ->
        SecurityBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                currentUser?.let { user ->
                    AccountSection(
                        user = user,
                        onLogout = { viewModel.logout() }
                    )
                }

                SecurityGlassCard(accent = Aqua) {
                    SecurityTag(text = stringResource(R.string.profile), color = Aqua)
                    SecuritySectionHeading(
                        eyebrow = stringResource(R.string.your_progress),
                        title = stringResource(R.string.profile_title),
                        description = stringResource(R.string.profile_description)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { completionRatio },
                                modifier = Modifier.size(122.dp),
                                strokeWidth = 12.dp,
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${(completionRatio * 100).toInt()}%",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = stringResource(R.string.progress_label),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SecurityMetricPill(
                                title = stringResource(R.string.metric_complete),
                                value = "${stats.completedCount}/${stats.totalScenarios}",
                                icon = Icons.Default.AutoAwesome
                            )
                            SecurityMetricPill(
                                title = stringResource(R.string.metric_attempts),
                                value = stats.totalAttempts.toString(),
                                icon = Icons.Default.Psychology
                            )
                            SecurityMetricPill(
                                title = stringResource(R.string.metric_hints),
                                value = stats.totalHintsUsed.toString(),
                                icon = Icons.Default.Lock
                            )
                        }
                    }
                }

                NextGoalSection(achievements = achievements)

                SecuritySectionHeading(
                    eyebrow = stringResource(R.string.achievements),
                    title = stringResource(R.string.achievement_title),
                    description = stringResource(R.string.achievement_description)
                )

                achievements.forEach { achievement ->
                    AchievementItem(achievement = achievement)
                }
            }
        }
    }
}

@Composable
private fun AccountSection(
    user: UserEntity,
    onLogout: () -> Unit
) {
    SecurityGlassCard(accent = Violet) {
        SecurityTag(
            text = stringResource(R.string.account_tagline),
            color = Violet
        )
        Text(
            text = user.displayName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecurityMetricPill(
                title = stringResource(R.string.member_since),
                value = user.createdAt.formatTimestamp(),
                icon = Icons.Default.AutoAwesome,
                modifier = Modifier.weight(1f)
            )
            SecurityMetricPill(
                title = stringResource(R.string.last_login),
                value = user.lastLoginAt.formatTimestamp(),
                icon = Icons.Default.Psychology,
                modifier = Modifier.weight(1f)
            )
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onLogout
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.logout),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun NextGoalSection(achievements: List<AchievementUiModel>) {
    val nextAchievement = achievements
        .filterNot { it.unlocked }
        .maxByOrNull { achievement -> achievement.progress.toFloat() / achievement.goal.toFloat() }

    SecurityGlassCard(accent = Violet) {
        SecurityTag(text = stringResource(R.string.next_goal), color = Violet)
        if (nextAchievement == null) {
            Text(
                text = stringResource(R.string.next_goal_master),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = nextAchievement.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = nextAchievement.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LinearProgressIndicator(
                progress = { nextAchievement.progress.toFloat() / nextAchievement.goal.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Violet,
                trackColor = Violet.copy(alpha = 0.14f)
            )
            Text(
                text = stringResource(
                    R.string.achievement_progress_value,
                    nextAchievement.progress,
                    nextAchievement.goal
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AchievementItem(achievement: AchievementUiModel) {
    val accent = if (achievement.unlocked) Mint else Sun

    SecurityGlassCard(accent = accent) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (achievement.unlocked) Icons.Default.Star else Icons.Default.Lock,
                contentDescription = if (achievement.unlocked) stringResource(R.string.unlocked) else stringResource(R.string.locked),
                tint = accent,
                modifier = Modifier.size(28.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LinearProgressIndicator(
                    progress = { achievement.progress.toFloat() / achievement.goal.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = accent,
                    trackColor = accent.copy(alpha = 0.14f)
                )
                Text(
                    text = if (achievement.unlocked && achievement.unlockedAt != null) {
                        stringResource(
                            R.string.achievement_unlocked_at,
                            achievement.unlockedAt.formatTimestamp()
                        )
                    } else {
                        stringResource(
                            R.string.achievement_progress_value,
                            achievement.progress,
                            achievement.goal
                        )
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun Long.formatTimestamp(): String {
    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(this))
}
