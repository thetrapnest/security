package ru.thetrapnest.security.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
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
    
    // Update progress
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isSuccess)
                    Icons.Default.CheckCircle
                else
                    Icons.Default.Error,
                contentDescription = if (isSuccess) stringResource(R.string.success) else stringResource(R.string.failure),
                tint = if (isSuccess)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (isSuccess) stringResource(R.string.attack_successful) else stringResource(R.string.attack_failed),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate("profile") }
            ) {
                Text(stringResource(R.string.view_profile))
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