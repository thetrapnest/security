package ru.thetrapnest.security.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.thetrapnest.security.R
import ru.thetrapnest.security.ui.components.SecurityBackground
import ru.thetrapnest.security.ui.components.SecurityGlassCard
import ru.thetrapnest.security.ui.components.SecuritySectionHeading
import ru.thetrapnest.security.ui.components.SecurityTag
import ru.thetrapnest.security.ui.theme.Coral
import ru.thetrapnest.security.ui.theme.Mint
import ru.thetrapnest.security.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    vulnerabilityType: String,
    vulnerabilityId: Int,
    userInput: String,
    navController: NavController,
    viewModel: SecurityViewModel
) {
    val (isSuccess, explanation) = when (vulnerabilityType) {
        "xss" -> checkXSS(userInput)
        "sqli" -> checkSQLi(userInput)
        "ldapi" -> checkLDAPi(userInput)
        "xpathi" -> checkXPathi(userInput)
        else -> false to stringResource(R.string.unknown_vulnerability)
    }
    val accent = if (isSuccess) Mint else Coral

    LaunchedEffect(isSuccess, vulnerabilityId) {
        if (isSuccess) {
            viewModel.markAsCompleted(vulnerabilityId)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        if (isSuccess) {
                            stringResource(R.string.attack_successful)
                        } else {
                            stringResource(R.string.attack_failed)
                        }
                    )
                },
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
        SecurityBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SecurityGlassCard(accent = accent) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(92.dp)
                        )
                    }
                    SecurityTag(
                        text = if (isSuccess) stringResource(R.string.success) else stringResource(R.string.failure),
                        color = accent
                    )
                    SecuritySectionHeading(
                        eyebrow = stringResource(R.string.what_happened),
                        title = if (isSuccess) {
                            stringResource(R.string.result_success_title)
                        } else {
                            stringResource(R.string.result_fail_title)
                        },
                        description = explanation
                    )
                }

                SecurityGlassCard(accent = accent) {
                    Text(
                        text = stringResource(R.string.how_to_improve),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(
                            if (isSuccess) R.string.next_step_success else R.string.next_step_fail
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SecurityTag(
                        text = if (isSuccess) {
                            stringResource(R.string.result_tag_success)
                        } else {
                            stringResource(R.string.result_tag_retry)
                        },
                        color = accent
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("practice/$vulnerabilityId") }
                    ) {
                        Text(stringResource(R.string.try_again))
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("profile") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.view_profile),
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
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
        input.contains("\") OR (\"1\"=\"1", ignoreCase = true)
    ) {
        true to stringResource(R.string.sqli_success)
    } else {
        false to stringResource(R.string.sqli_fail)
    }
}

@Composable
fun checkLDAPi(input: String): Pair<Boolean, String> {
    return if (input.contains("*)(|(uid=*))", ignoreCase = true) ||
        input.contains("*)(uid=*))(|(uid=*", ignoreCase = true) ||
        input.contains("*))(|(uid=*", ignoreCase = true) ||
        input.contains("*)(|(cn=*))", ignoreCase = true)
    ) {
        true to stringResource(R.string.ldap_success)
    } else {
        false to stringResource(R.string.ldap_fail)
    }
}

@Composable
fun checkXPathi(input: String): Pair<Boolean, String> {
    return if (input.contains("' or '1'='1", ignoreCase = true) ||
        input.contains("' or 1=1", ignoreCase = true) ||
        input.contains("\" or \"1\"=\"1", ignoreCase = true) ||
        input.contains("\" or 1=1", ignoreCase = true) ||
        input.contains("' or true()", ignoreCase = true) ||
        input.contains("\" or true()", ignoreCase = true)
    ) {
        true to stringResource(R.string.xpath_success)
    } else {
        false to stringResource(R.string.xpath_fail)
    }
}
