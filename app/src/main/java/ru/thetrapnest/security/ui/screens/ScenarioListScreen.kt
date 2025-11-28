package ru.thetrapnest.security.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.thetrapnest.security.R
import ru.thetrapnest.security.database.VulnerabilityEntity
import ru.thetrapnest.security.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioListScreen(
    navController: NavController,
    viewModel: SecurityViewModel = viewModel()
) {
    val vulnerabilities by viewModel.vulnerabilities.collectAsState(initial = emptyList())
    val progressList by viewModel.userProgress.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.security_scenarios)) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(vulnerabilities) { vulnerability ->
                val progress = progressList.find { it.vulnerabilityId == vulnerability.id }
                ScenarioItem(
                    vulnerability = vulnerability,
                    isCompleted = progress?.completed == true,
                    onScenarioClick = { navController.navigate("theory/${vulnerability.id}") }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ScenarioItem(
    vulnerability: VulnerabilityEntity,
    isCompleted: Boolean,
    onScenarioClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onScenarioClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vulnerability.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = vulnerability.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.completed),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}