package ru.thetrapnest.security.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.thetrapnest.security.R
import ru.thetrapnest.security.database.VulnerabilityEntity
import ru.thetrapnest.security.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    vulnerabilityId: Int,
    navController: NavController,
    viewModel: SecurityViewModel = viewModel()
) {
    val vulnerability by viewModel.getVulnerabilityById(vulnerabilityId).collectAsState(initial = null)
    val progressList by viewModel.userProgress.collectAsState()
    val progress = progressList.find { it.vulnerabilityId == vulnerabilityId }
    var userInput by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    vulnerability?.let { vuln ->
        Scaffold(
            topBar = {
                TopAppBar(
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
                Surface(shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.attempt_counter, progress?.attempts ?: 0),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.hint_counter, progress?.hintsUsed ?: 0),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.incrementAttempts(vuln.id)
                                when (vuln.type) {
                                    ru.thetrapnest.security.data.VulnerabilityType.XSS -> {
                                        navController.navigate("result/xss/${vuln.id}?input=${userInput}")
                                    }
                                    ru.thetrapnest.security.data.VulnerabilityType.SQLI -> {
                                        navController.navigate("result/sqli/${vuln.id}?input=${userInput}")
                                    }
                                }
                            }
                        ) {
                            Text(stringResource(R.string.attack))
                        }
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                coroutineScope.launch {
                                    showHint = true
                                    viewModel.incrementHintsUsed(vuln.id)
                                }
                            }
                        ) {
                            Text(stringResource(R.string.hint))
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PracticeHero()
                StepTitle(stringResource(R.string.step_input))
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.input)) }
                )

                StepTitle(stringResource(R.string.step_hint))
                Text(
                    text = stringResource(R.string.hint_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                StepTitle(stringResource(R.string.step_simulation))
                SimulationCarousel(vulnerability = vuln, userInput = userInput)

                StepTitle(stringResource(R.string.step_action))
                Text(
                    text = stringResource(R.string.action_instruction),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (showHint) {
                ModalBottomSheet(
                    onDismissRequest = { showHint = false },
                    sheetState = sheetState
                ) {
                    HintSheet(hint = vuln.hint, onClose = { showHint = false })
                }
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.loading))
        }
    }
}

@Composable
private fun PracticeHero() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_console_simulation),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.step_simulation),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.action_instruction),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun StepTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SimulationCarousel(vulnerability: VulnerabilityEntity, userInput: String) {
    val cards = when (vulnerability.type) {
        ru.thetrapnest.security.data.VulnerabilityType.XSS -> listOf(
            SimulationCardContent(
                title = stringResource(R.string.web_page_simulation),
                description = stringResource(R.string.web_page_simulation_desc)
            ) {
                XSSSimulation(userInput = userInput)
            },
            SimulationCardContent(
                title = stringResource(R.string.preview_payload),
                description = stringResource(R.string.preview_payload_desc)
            ) {
                Text(
                    text = userInput.ifEmpty { stringResource(R.string.empty_payload) },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        )

        ru.thetrapnest.security.data.VulnerabilityType.SQLI -> listOf(
            SimulationCardContent(
                title = stringResource(R.string.db_query_simulation),
                description = stringResource(R.string.db_query_simulation_desc)
            ) {
                SQLiSimulation(userInput = userInput)
            },
            SimulationCardContent(
                title = stringResource(R.string.preview_payload),
                description = stringResource(R.string.preview_payload_desc)
            ) {
                Text(
                    text = "SELECT * FROM users WHERE login = '$userInput' AND password = '...'",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        )
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cards) { card ->
            Card(
                modifier = Modifier
                    .width(280.dp)
                    .heightIn(min = 220.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = card.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = card.description, style = MaterialTheme.typography.bodyMedium)
                    card.content()
                }
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
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.hint_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = hint, style = MaterialTheme.typography.bodyMedium)
        Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.close_hint))
        }
    }
}

@Composable
fun XSSSimulation(userInput: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.web_page_simulation),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.web_page_simulation_desc),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                    }
                },
                update = { webView ->
                    webView.loadData(
                        "<html><body><div>$userInput</div></body></html>",
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
}

@Composable
fun SQLiSimulation(userInput: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.db_query_simulation),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.db_query_simulation_desc),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SELECT * FROM users WHERE login = '$userInput' AND password = '...'",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
