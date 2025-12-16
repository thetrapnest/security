package ru.thetrapnest.security.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.thetrapnest.security.R
import ru.thetrapnest.security.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    vulnerabilityType: String,
    vulnerabilityId: Int,
    userInput: String,
    navController: NavController,
    viewModel: SecurityViewModel = viewModel()
) {
    val (isSuccess, explanation) = when (vulnerabilityType) {
        "xss" -> checkXSS(userInput)
        "sqli" -> checkSQLi(userInput)
        else -> false to stringResource(R.string.unknown_vulnerability)
    }

    LaunchedEffect(Unit) {
        if (isSuccess) {
            viewModel.markAsCompleted(vulnerabilityId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isSuccess) stringResource(R.string.attack_successful) else stringResource(R.string.attack_failed)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
                color = if (isSuccess) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
            ) {
                Icon(
                    imageVector = if (isSuccess)
                        Icons.Default.CheckCircle
                    else
                        Icons.Default.Error,
                    contentDescription = if (isSuccess) stringResource(R.string.success) else stringResource(R.string.failure),
                    tint = if (isSuccess)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .size(120.dp)
                        .padding(20.dp)
                )
            }

            Text(
                text = if (isSuccess) stringResource(R.string.attack_successful) else stringResource(R.string.attack_failed),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = stringResource(R.string.what_happened), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(text = explanation, style = MaterialTheme.typography.bodyLarge)
                    Divider()
                    Text(text = stringResource(R.string.how_to_improve), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = stringResource(
                            if (isSuccess) R.string.next_step_success else R.string.next_step_fail
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(modifier = Modifier.weight(1f), onClick = { navController.navigate("practice/$vulnerabilityId") }) {
                    Text(stringResource(R.string.try_again))
                }
                Button(modifier = Modifier.weight(1f), onClick = { navController.navigate("profile") }) {
                    Text(stringResource(R.string.view_profile))
                }
            }
        }
    }
}

@Composable
fun checkXSS(input: String): Pair<Boolean, String> {
    return if (input.contains("<script>", ignoreCase = true)) {
        true to stringResource(R.string.xss_success)
    } else {
        false to stringResource(R.string.xss_fail)
    }
}

@Composable
fun checkSQLi(input: String): Pair<Boolean, String> {
    return if (input.contains("' OR '1'='1", ignoreCase = true) ||
        input.contains("' OR 1=1", ignoreCase = true) ||
        input.contains("\" OR \"1\"=\"1", ignoreCase = true) ||
        input.contains("\" OR 1=1", ignoreCase = true) ||
        input.contains("') OR ('1'='1", ignoreCase = true) ||
        input.contains("\") OR (\"1\"=\"1", ignoreCase = true)) {
        true to stringResource(R.string.sqli_success)
    } else {
        false to stringResource(R.string.sqli_fail)
    }
}
