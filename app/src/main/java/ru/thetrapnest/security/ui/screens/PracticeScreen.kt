package ru.thetrapnest.security.ui.screens

import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import ru.thetrapnest.security.R
import ru.thetrapnest.security.data.VulnerabilityType
import ru.thetrapnest.security.database.VulnerabilityEntity
import ru.thetrapnest.security.ui.components.SecurityBackground
import ru.thetrapnest.security.ui.components.SecurityGlassCard
import ru.thetrapnest.security.ui.components.SecurityMetricPill
import ru.thetrapnest.security.ui.components.SecuritySectionHeading
import ru.thetrapnest.security.ui.components.SecurityTag
import ru.thetrapnest.security.ui.theme.Aqua
import ru.thetrapnest.security.ui.theme.Coral
import ru.thetrapnest.security.ui.theme.Mint
import ru.thetrapnest.security.ui.theme.Violet
import ru.thetrapnest.security.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    vulnerabilityId: Int,
    navController: NavController,
    viewModel: SecurityViewModel
) {
    val vulnerability by viewModel.getVulnerabilityById(vulnerabilityId).collectAsState(initial = null)
    val progressList by viewModel.userProgress.collectAsState(initial = emptyList())
    val progress = progressList.find { it.vulnerabilityId == vulnerabilityId }
    var userInput by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(false) }

    vulnerability?.let { vuln ->
        val accent = practiceAccent(vuln.type)

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = { Text(stringResource(R.string.practice_title, vuln.title)) },
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
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { showHint = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.hint),
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val encodedInput = Uri.encode(userInput)
                            viewModel.incrementAttempts(vuln.id)
                            when (vuln.type) {
                                VulnerabilityType.XSS -> navController.navigate("result/xss/${vuln.id}?input=$encodedInput")
                                VulnerabilityType.SQLI -> navController.navigate("result/sqli/${vuln.id}?input=$encodedInput")
                                VulnerabilityType.LDAPI -> navController.navigate("result/ldapi/${vuln.id}?input=$encodedInput")
                                VulnerabilityType.XPATHI -> navController.navigate("result/xpathi/${vuln.id}?input=$encodedInput")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.attack),
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
                    SecurityGlassCard(accent = accent) {
                        SecurityTag(text = stringResource(R.string.practice_label), color = accent)
                        SecuritySectionHeading(
                            eyebrow = stringResource(R.string.step_input),
                            title = stringResource(R.string.practice_hero_title),
                            description = stringResource(R.string.practice_hero_description)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SecurityMetricPill(
                                title = stringResource(R.string.metric_attempts),
                                value = (progress?.attempts ?: 0).toString(),
                                icon = Icons.Default.Bolt,
                                modifier = Modifier.weight(1f)
                            )
                            SecurityMetricPill(
                                title = stringResource(R.string.metric_hints),
                                value = (progress?.hintsUsed ?: 0).toString(),
                                icon = Icons.Default.Lightbulb,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    PracticeBlock(
                        step = stringResource(R.string.step_input),
                        accent = accent
                    ) {
                        Text(
                            text = stringResource(R.string.enter_payload),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = userInput,
                            onValueChange = { userInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.input)) },
                            maxLines = 4
                        )
                    }

                    PracticeBlock(
                        step = stringResource(R.string.step_hint),
                        accent = Violet
                    ) {
                        Text(
                            text = stringResource(R.string.hint_prompt),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        SecurityTag(
                            text = stringResource(R.string.hint_ready),
                            color = Violet
                        )
                    }

                    PracticeBlock(
                        step = stringResource(R.string.step_simulation),
                        accent = Mint
                    ) {
                        Text(
                            text = stringResource(R.string.simulation_description),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        SimulationCarousel(vulnerability = vuln, userInput = userInput)
                    }

                    PracticeBlock(
                        step = stringResource(R.string.step_action),
                        accent = Coral
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Coral
                            )
                            Text(
                                text = stringResource(R.string.action_instruction),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                if (showHint) {
                    LaunchedEffect(vuln.id) {
                        viewModel.incrementHintsUsed(vuln.id)
                    }
                    ModalBottomSheet(
                        onDismissRequest = { showHint = false }
                    ) {
                        HintSheet(hint = vuln.hint, onClose = { showHint = false })
                    }
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
private fun PracticeBlock(
    step: String,
    accent: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    SecurityGlassCard(accent = accent) {
        SecurityTag(text = step, color = accent)
        content()
    }
}

@Composable
private fun SimulationCarousel(
    vulnerability: VulnerabilityEntity,
    userInput: String
) {
    val cards = when (vulnerability.type) {
        VulnerabilityType.XSS -> listOf(
            SimulationCardContent(
                title = stringResource(R.string.web_page_simulation),
                description = stringResource(R.string.web_page_simulation_desc)
            ) { XSSSimulation(userInput = userInput) },
            SimulationCardContent(
                title = stringResource(R.string.preview_payload),
                description = stringResource(R.string.preview_payload_desc)
            ) {
                PayloadPreview(
                    text = userInput.ifEmpty { stringResource(R.string.empty_payload) },
                    accent = Aqua
                )
            }
        )

        VulnerabilityType.SQLI -> listOf(
            SimulationCardContent(
                title = stringResource(R.string.db_query_simulation),
                description = stringResource(R.string.db_query_simulation_desc)
            ) { SQLiSimulation(userInput = userInput) },
            SimulationCardContent(
                title = stringResource(R.string.preview_payload),
                description = stringResource(R.string.preview_payload_desc)
            ) {
                PayloadPreview(
                    text = "SELECT * FROM users WHERE login = '$userInput' AND password = '...'",
                    accent = Violet
                )
            }
        )

        VulnerabilityType.LDAPI -> listOf(
            SimulationCardContent(
                title = stringResource(R.string.ldap_query_simulation),
                description = stringResource(R.string.ldap_query_simulation_desc)
            ) { LDAPiSimulation(userInput = userInput) },
            SimulationCardContent(
                title = stringResource(R.string.preview_payload),
                description = stringResource(R.string.preview_payload_desc)
            ) {
                PayloadPreview(
                    text = "(&(uid=$userInput)(userPassword=...))",
                    accent = Mint
                )
            }
        )

        VulnerabilityType.XPATHI -> listOf(
            SimulationCardContent(
                title = stringResource(R.string.xpath_query_simulation),
                description = stringResource(R.string.xpath_query_simulation_desc)
            ) { XPathSimulation(userInput = userInput) },
            SimulationCardContent(
                title = stringResource(R.string.preview_payload),
                description = stringResource(R.string.preview_payload_desc)
            ) {
                PayloadPreview(
                    text = "//users/user[login/text()='$userInput' and password/text()='...']",
                    accent = Coral
                )
            }
        )
    }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(cards) { card ->
            SecurityGlassCard(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .width(320.dp),
                accent = practiceAccent(vulnerability.type)
            ) {
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = card.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                card.content()
            }
        }
    }
}

private data class SimulationCardContent(
    val title: String,
    val description: String,
    val content: @Composable () -> Unit
)

@Composable
private fun HintSheet(hint: String, onClose: () -> Unit) {
    SecurityGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        accent = Violet
    ) {
        SecurityTag(
            text = stringResource(R.string.hint_title),
            color = Violet
        )
        Text(
            text = hint,
            style = MaterialTheme.typography.bodyLarge
        )
        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.close_hint))
        }
    }
}

@Composable
private fun PayloadPreview(
    text: String,
    accent: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = accent.copy(alpha = 0.08f),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = accent
        )
    }
}

@Composable
fun XSSSimulation(userInput: String) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        PayloadPreview(
            text = stringResource(R.string.live_preview),
            accent = Aqua
        )
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                }
            },
            update = { webView ->
                webView.loadData(
                    "<html><body style=\"font-family:sans-serif;padding:18px;background:#091120;color:#d7e8ff;\"><div>$userInput</div></body></html>",
                    "text/html",
                    "UTF-8"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}

@Composable
fun SQLiSimulation(userInput: String) {
    PayloadPreview(
        text = "SELECT * FROM users WHERE login = '$userInput' AND password = '...'",
        accent = Violet
    )
}

@Composable
fun LDAPiSimulation(userInput: String) {
    PayloadPreview(
        text = "(&(uid=$userInput)(userPassword=...))",
        accent = Mint
    )
}

@Composable
fun XPathSimulation(userInput: String) {
    PayloadPreview(
        text = "//users/user[login/text()='$userInput' and password/text()='...']",
        accent = Coral
    )
}

private fun practiceAccent(type: VulnerabilityType): Color = when (type) {
    VulnerabilityType.XSS -> Aqua
    VulnerabilityType.SQLI -> Violet
    VulnerabilityType.LDAPI -> Mint
    VulnerabilityType.XPATHI -> Coral
}
