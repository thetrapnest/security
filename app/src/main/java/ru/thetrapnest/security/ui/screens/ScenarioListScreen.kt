package ru.thetrapnest.security.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.thetrapnest.security.R
import ru.thetrapnest.security.data.VulnerabilityType
import ru.thetrapnest.security.database.VulnerabilityEntity
import ru.thetrapnest.security.ui.components.SecurityBackground
import ru.thetrapnest.security.ui.components.SecurityBottomNav
import ru.thetrapnest.security.ui.components.SecurityGlassCard
import ru.thetrapnest.security.ui.components.SecurityMetricPill
import ru.thetrapnest.security.ui.components.SecuritySectionHeading
import ru.thetrapnest.security.ui.components.SecurityTag
import ru.thetrapnest.security.ui.theme.Aqua
import ru.thetrapnest.security.ui.theme.Coral
import ru.thetrapnest.security.ui.theme.Mint
import ru.thetrapnest.security.ui.theme.Sun
import ru.thetrapnest.security.ui.theme.Violet
import ru.thetrapnest.security.viewmodel.SecurityViewModel

@Composable
fun ScenarioListScreen(
    navController: NavController,
    viewModel: SecurityViewModel
) {
    val vulnerabilities by viewModel.vulnerabilities.collectAsState(initial = emptyList())
    val progressList by viewModel.userProgress.collectAsState(initial = emptyList())
    val completedCount = progressList.count { it.completed }
    val completionRatio = if (vulnerabilities.isNotEmpty()) {
        completedCount.toFloat() / vulnerabilities.size.toFloat()
    } else {
        0f
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            SecurityBottomNav(
                selectedRoute = "scenarios",
                onScenariosClick = {},
                onProfileClick = { navController.navigate("profile") }
            )
        }
    ) { paddingValues ->
        SecurityBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 20.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    HeroSection(
                        totalScenarios = vulnerabilities.size,
                        completedCount = completedCount,
                        completionRatio = completionRatio,
                        onProfileClick = { navController.navigate("profile") }
                    )
                }

                item {
                    SecuritySectionHeading(
                        eyebrow = stringResource(R.string.security_scenarios),
                        title = stringResource(R.string.dashboard_title),
                        description = stringResource(R.string.dashboard_description)
                    )
                }

                items(vulnerabilities, key = { it.id }) { vulnerability ->
                    val progress = progressList.find { it.vulnerabilityId == vulnerability.id }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ScenarioItem(
                            vulnerability = vulnerability,
                            isCompleted = progress?.completed == true,
                            attempts = progress?.attempts ?: 0,
                            hintsUsed = progress?.hintsUsed ?: 0,
                            onScenarioClick = { navController.navigate("theory/${vulnerability.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroSection(
    totalScenarios: Int,
    completedCount: Int,
    completionRatio: Float,
    onProfileClick: () -> Unit
) {
    SecurityGlassCard(
        accent = Aqua
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecurityTag(
                    text = stringResource(R.string.hero_tagline),
                    color = Aqua
                )
                Text(
                    text = stringResource(R.string.hero_title),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = stringResource(R.string.hero_description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.ArrowOutward,
                    contentDescription = stringResource(R.string.view_profile),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        LinearProgressIndicator(
            progress = completionRatio,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecurityMetricPill(
                title = stringResource(R.string.metric_total),
                value = totalScenarios.toString(),
                icon = Icons.Default.Security,
                modifier = Modifier
                    .weight(1f)
                    .width(0.dp)
            )
            SecurityMetricPill(
                title = stringResource(R.string.metric_complete),
                value = completedCount.toString(),
                icon = Icons.Default.Bolt,
                modifier = Modifier
                    .weight(1f)
                    .width(0.dp)
            )
        }
    }
}

@Composable
fun ScenarioItem(
    vulnerability: VulnerabilityEntity,
    isCompleted: Boolean,
    attempts: Int,
    hintsUsed: Int,
    onScenarioClick: () -> Unit
) {
    val accent = vulnerability.type.accentColor()
    val typeLabel = vulnerability.type.label()
    val typeIcon = vulnerability.type.icon()
    val activityValue = (attempts + hintsUsed).coerceAtLeast(1)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onScenarioClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            accent.copy(alpha = 0.14f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.12f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                        )
                    )
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(accent.copy(alpha = 0.15f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = typeIcon,
                        contentDescription = null,
                        tint = accent
                    )
                }
                SecurityTag(
                    text = if (isCompleted) stringResource(R.string.completed) else stringResource(R.string.in_progress),
                    color = if (isCompleted) Mint else Sun
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = vulnerability.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = vulnerability.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SecurityTag(text = typeLabel, color = accent)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.activity_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.attempt_hint_compact, attempts, hintsUsed),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                LinearProgressIndicator(
                    progress = (activityValue / 6f).coerceAtMost(1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    color = accent,
                    trackColor = accent.copy(alpha = 0.16f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.open_scenario),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
                Icon(
                    imageVector = Icons.Default.ArrowOutward,
                    contentDescription = null,
                    tint = accent
                )
            }
        }
    }
}

@Composable
private fun VulnerabilityType.label(): String = when (this) {
    VulnerabilityType.XSS -> stringResource(R.string.vulnerability_type_xss)
    VulnerabilityType.SQLI -> stringResource(R.string.vulnerability_type_sqli)
    VulnerabilityType.LDAPI -> stringResource(R.string.vulnerability_type_ldapi)
    VulnerabilityType.XPATHI -> stringResource(R.string.vulnerability_type_xpathi)
}

private fun VulnerabilityType.accentColor(): Color = when (this) {
    VulnerabilityType.XSS -> Aqua
    VulnerabilityType.SQLI -> Violet
    VulnerabilityType.LDAPI -> Mint
    VulnerabilityType.XPATHI -> Coral
}

private fun VulnerabilityType.icon(): ImageVector = when (this) {
    VulnerabilityType.XSS -> Icons.Default.Code
    VulnerabilityType.SQLI -> Icons.Default.Storage
    VulnerabilityType.LDAPI -> Icons.Default.AccountTree
    VulnerabilityType.XPATHI -> Icons.Default.Bolt
}
