package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.data.model.GradeCatalog
import com.example.data.model.UserProfile
import com.example.data.repository.BrightQuizRepository
import com.example.data.repository.signInForGeminiWithGoogle
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.OnCoralContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.OnSunnyGold
import com.example.ui.theme.SunnyGoldLight
import com.example.ui.viewmodel.AskTabState
import com.example.util.AppStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AskAiScreen(
    profile: UserProfile,
    askState: AskTabState,
    strings: AppStrings = AppStrings.forLanguage(profile.language),
    onCompleteGeminiSignIn: (email: String, displayName: String) -> Unit,
    onSignOutGemini: () -> Unit,
    onSendQuestion: (String) -> Unit
) {
    if (!askState.isGeminiSignedIn) {
        GeminiSignInGateCard(
            profile = profile,
            initialError = askState.signInError,
            strings = strings,
            onSignedIn = onCompleteGeminiSignIn
        )
        return
    }

    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val todayKey = BrightQuizRepository.currentDayKey()
    val weekKey = BrightQuizRepository.currentWeekKey()
    val monthKey = BrightQuizRepository.currentMonthKey()

    val dailyCount = if (profile.askDayKey == todayKey) profile.dailyAskCount else 0
    val weeklyCount = if (profile.askWeekKey == weekKey) profile.weeklyAskCount else 0
    val monthlyCount = if (profile.askMonthKey == monthKey) profile.monthlyAskCount else 0

    val displayMessages = remember(askState.messages, strings.askWelcome) {
        if (askState.messages.isEmpty()) {
            listOf(
                com.example.data.model.ChatMessage(
                    id = "welcome",
                    fromUser = false,
                    text = strings.askWelcome
                )
            )
        } else {
            askState.messages
        }
    }

    LaunchedEffect(displayMessages.size, askState.streamingText) {
        if (displayMessages.isNotEmpty()) {
            listState.animateScrollToItem(displayMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Study Bonus Progress Card + Connected Gemini Indicator
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = strings.askStudyOnlyTitle,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    TextButton(
                        onClick = onSignOutGemini,
                        modifier = Modifier.testTag("gemini_switch_account_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldCorrect,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Gemini", style = MaterialTheme.typography.labelMedium)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BonusProgressChip(
                        label = "Day $dailyCount/5",
                        reward = "+15c",
                        progress = (dailyCount / 5f).coerceIn(0f, 1f),
                        claimed = profile.dailyBonusDayKey == todayKey,
                        modifier = Modifier.weight(1f)
                    )
                    BonusProgressChip(
                        label = "Week $weeklyCount/25",
                        reward = "+75c",
                        progress = (weeklyCount / 25f).coerceIn(0f, 1f),
                        claimed = profile.weeklyBonusWeekKey == weekKey,
                        modifier = Modifier.weight(1f)
                    )
                    BonusProgressChip(
                        label = "Month $monthlyCount/50",
                        reward = "+100c",
                        progress = (monthlyCount / 50f).coerceIn(0f, 1f),
                        claimed = profile.monthlyBonusMonthKey == monthKey,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (askState.latestBonusMessage != null) {
            Spacer(Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = EmeraldContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = askState.latestBonusMessage,
                    color = OnEmeraldContainer,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Quick Study Prompts tailored to student's grade
        val studentGrade = GradeCatalog.normalizeGrade(profile.grade)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                "Explain a key science topic for $studentGrade",
                "Tips for Monthly GK Test",
                "Help me practice $studentGrade math formulas"
            ).forEach { prompt ->
                AssistChip(
                    onClick = { if (!askState.isStreaming) onSendQuestion(prompt) },
                    label = { Text(prompt, style = MaterialTheme.typography.labelMedium) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Chat Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(displayMessages, key = { it.id }) { msg ->
                val isUser = msg.fromUser
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        ),
                        color = when {
                            isUser -> MaterialTheme.colorScheme.primary
                            msg.offTopic -> CoralContainer
                            else -> MaterialTheme.colorScheme.surface
                        },
                        border = if (!isUser && !msg.offTopic) {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        } else null,
                        modifier = Modifier.widthIn(max = 310.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (msg.offTopic) {
                                Text(
                                    text = "Books, Tests & Studies Only",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnCoralContainer
                                )
                                Spacer(Modifier.height(4.dp))
                            }
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = when {
                                    isUser -> Color.White
                                    msg.offTopic -> OnCoralContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }

            if (askState.isStreaming) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.widthIn(max = 310.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (askState.streamingText.isBlank()) {
                                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Spacer(Modifier.width(10.dp))
                                    Text("Gemini is thinking about your study question...")
                                } else {
                                    Text(
                                        text = askState.streamingText,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (askState.errorMessage != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CoralContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Text(
                    text = askState.errorMessage,
                    color = OnCoralContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        // Question Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text(strings.askInputPlaceholder) },
                singleLine = false,
                maxLines = 3,
                modifier = Modifier
                    .weight(1f)
                    .testTag("ask_ai_input")
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val toSend = input.trim()
                    if (toSend.isNotEmpty() && !askState.isStreaming) {
                        input = ""
                        onSendQuestion(toSend)
                    }
                },
                enabled = input.isNotBlank() && !askState.isStreaming,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .size(52.dp)
                    .testTag("ask_ai_send_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send study question")
            }
        }
    }
}

@Composable
private fun GeminiSignInGateCard(
    profile: UserProfile,
    initialError: String?,
    strings: AppStrings,
    onSignedIn: (email: String, displayName: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var isSigningIn by remember { mutableStateOf(false) }
    var signInError by remember(initialError) { mutableStateOf(initialError) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(72.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "Gemini AI",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = strings.geminiSignInTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = strings.geminiSignInSubtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = SunnyGoldLight
                ) {
                    Text(
                        text = "Books, Studies & Tests Only • ${GradeCatalog.normalizeGrade(profile.grade)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSunnyGold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                if (signInError != null) {
                    Spacer(Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CoralContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = signInError ?: "",
                            color = OnCoralContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))
                Button(
                    onClick = {
                        isSigningIn = true
                        signInError = null
                        signInForGeminiWithGoogle(
                            context = context,
                            credentialManager = credentialManager,
                            scope = scope,
                            onSuccess = { email, displayName ->
                                isSigningIn = false
                                onSignedIn(email, displayName)
                            },
                            onError = { err ->
                                isSigningIn = false
                                signInError = err
                            },
                            onCancelled = { msg ->
                                isSigningIn = false
                                signInError = msg
                            }
                        )
                    },
                    enabled = !isSigningIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("gemini_sign_in_button")
                ) {
                    if (isSigningIn) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Signing in to Gemini...")
                    } else {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(strings.geminiSignInButton)
                    }
                }
            }
        }
    }
}

@Composable
private fun BonusProgressChip(
    label: String,
    reward: String,
    progress: Float,
    claimed: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (claimed) EmeraldContainer else SunnyGoldLight,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (claimed) OnEmeraldContainer else OnSunnyGold
                )
                Text(
                    text = if (claimed) "Won!" else reward,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (claimed) EmeraldCorrect else OnSunnyGold
                )
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}
