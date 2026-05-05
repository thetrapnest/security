package ru.thetrapnest.security.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import ru.thetrapnest.security.ui.components.SecurityBackground
import ru.thetrapnest.security.ui.components.SecurityGlassCard
import ru.thetrapnest.security.ui.components.SecuritySectionHeading
import ru.thetrapnest.security.ui.components.SecurityTag
import ru.thetrapnest.security.ui.theme.Aqua
import ru.thetrapnest.security.ui.theme.Mint
import ru.thetrapnest.security.ui.theme.Violet
import ru.thetrapnest.security.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheoryScreen(
    vulnerabilityId: Int,
    navController: NavController,
    viewModel: SecurityViewModel
) {
    val vulnerability by viewModel.getVulnerabilityById(vulnerabilityId).collectAsState(initial = null)

    vulnerability?.let { vuln ->
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent
                    ),
                    title = { Text(vuln.title) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { navController.popBackStack() }
                    ) {
                        Text(stringResource(R.string.back))
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("practice/${vuln.id}") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.go_to_practice),
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
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
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SecurityGlassCard(accent = Aqua) {
                        SecurityTag(text = stringResource(R.string.learning_path), color = Aqua)
                        SecuritySectionHeading(
                            eyebrow = stringResource(R.string.theory),
                            title = vuln.title,
                            description = vuln.description
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SecurityTag(
                                text = stringResource(R.string.step_indicator, 1, 3),
                                color = Violet
                            )
                            SecurityTag(
                                text = stringResource(R.string.theory_focus),
                                color = Mint
                            )
                        }
                    }

                    TheorySection(
                        title = stringResource(R.string.description),
                        body = vuln.description,
                        accent = Aqua
                    )
                    TheorySection(
                        title = stringResource(R.string.theory),
                        body = vuln.theory,
                        accent = Violet
                    )
                    TheorySection(
                        title = stringResource(R.string.example),
                        body = vuln.example,
                        accent = Mint
                    )
                }
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.loading))
    }
}

@Composable
private fun TheorySection(
    title: String,
    body: String,
    accent: androidx.compose.ui.graphics.Color
) {
    SecurityGlassCard(accent = accent) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
