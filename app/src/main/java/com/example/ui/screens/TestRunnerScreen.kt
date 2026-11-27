package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.TestAttempt
import com.example.data.model.TestType
import com.example.data.repository.base64ToBitmap
import com.example.ui.components.CertificateDialog
import com.example.ui.components.formatAttemptDate
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.CoralWrong
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.OnCoralContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.OnSunnyGold
import com.example.ui.theme.SunnyGold
import com.example.ui.theme.SunnyGoldLight
import com.example.ui.viewmodel.ActiveTestSession
import java.util.Locale

@Composable
fun TestRunnerScreen(
    session: ActiveTestSession,
    onSelectOption: (Int) -> Unit,
    onNextOrFinish: () -> Unit,
    onCloseResult: () -> Unit
) {
    val isTimed = session.testType.durationSeconds > 0
    var showBackBlockedNotice by remember { mutableStateOf(false) }

    // Anti-cheat: strictly block back navigation during active timed tests!
    if (session.finishedAttempt == null) {
        BackHandler(enabled = true) {
            if (isTimed) {
                showBackBlockedNotice = true
            } else {
                onCloseResult()
            }
        }
    }

    val attempt = session.finishedAttempt
    if (attempt != null) {
        TestResultSummaryScreen(
            attempt = attempt,
            onDone = onCloseResult
        )
        return
    }

    if (session.isSubmittingResult) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeDrawingPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Calculating your score & coins...", style = MaterialTheme.typography.titleMedium)
            }
        }
        return
    }

    val question = session.questions.getOrNull(session.currentIndex) ?: return
    val mins = session.remainingSeconds / 60
    val secs = session.remainingSeconds % 60
    val timerText = String.format(Locale.US, "%02d:%02d", mins, secs)
    val isLowTime = isTimed && session.remainingSeconds <= 15

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header Bar — NO BACK BUTTON during timed tests!
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.testType.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Question ${session.currentIndex + 1} of ${session.questions.size} • ${question.category.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isTimed) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isLowTime) CoralContainer else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = "Remaining Time",
                            tint = if (isLowTime) CoralWrong else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = timerText,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = if (isLowTime) OnCoralContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.testTag("test_countdown_timer")
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onCloseResult,
                    modifier = Modifier.testTag("exit_practice_button")
                ) {
                    Text("Exit Practice")
                }
            }
        }

        if (isTimed) {
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (showBackBlockedNotice) {
                        "Back navigation is locked during timed tests to keep scores fair."
                    } else {
                        "Anti-cheat mode active: No back option during timed tests."
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = if (showBackBlockedNotice) CoralWrong else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { (session.currentIndex + 1).toFloat() / session.questions.size.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Spacer(Modifier.height(16.dp))

        // Question Card + Optional Picture
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                val bmp = remember(question.imageBase64) { base64ToBitmap(question.imageBase64) }
                if (bmp != null) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Question illustration",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(14.dp))
                } else if (question.localImageRes != null) {
                    Image(
                        painter = painterResource(id = question.localImageRes),
                        contentDescription = "Current affairs question illustration",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(14.dp))
                }

                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Options List — Incorrect turns RED and Correct turns GREEN automatically!
        val chosen = session.selectedOptionIndex
        val answered = chosen != null
        question.options.forEachIndexed { index, optionText ->
            val isCorrectOption = index == question.correctIndex
            val isChosenOption = index == chosen

            val containerColor = when {
                !answered -> MaterialTheme.colorScheme.surface
                isCorrectOption -> EmeraldContainer
                isChosenOption && !isCorrectOption -> CoralContainer
                else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            }
            val borderColor = when {
                !answered -> MaterialTheme.colorScheme.outline
                isCorrectOption -> EmeraldCorrect
                isChosenOption && !isCorrectOption -> CoralWrong
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            }
            val textColor = when {
                !answered -> MaterialTheme.colorScheme.onSurface
                isCorrectOption -> OnEmeraldContainer
                isChosenOption && !isCorrectOption -> OnCoralContainer
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = containerColor,
                border = BorderStroke(if (answered && (isCorrectOption || isChosenOption)) 2.dp else 1.dp, borderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(enabled = !answered) { onSelectOption(index) }
                    .testTag("quiz_option_$index")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = borderColor.copy(alpha = 0.16f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${'A' + index}",
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = optionText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (answered && isCorrectOption) FontWeight.Bold else FontWeight.Medium,
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )
                    if (answered && isCorrectOption) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Correct answer",
                            tint = EmeraldCorrect
                        )
                    } else if (answered && isChosenOption && !isCorrectOption) {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = "Wrong answer",
                            tint = CoralWrong
                        )
                    }
                }
            }
        }

        if (answered) {
            Spacer(Modifier.height(12.dp))
            if (question.explanation.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Fact: ${question.explanation}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(14.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            Button(
                onClick = onNextOrFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("quiz_next_button")
            ) {
                val isLast = session.currentIndex + 1 >= session.questions.size
                Text(if (isLast) "Finish & View Results" else "Next Question")
            }
        }
    }
}

@Composable
private fun TestResultSummaryScreen(
    attempt: TestAttempt,
    onDone: () -> Unit
) {
    var showCertificate by remember { mutableStateOf(false) }
    val percent = if (attempt.totalQuestions > 0) {
        (attempt.correctAnswers * 100) / attempt.totalQuestions
    } else 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (attempt.passed) Icons.Filled.Verified else Icons.Filled.Timer,
                    contentDescription = null,
                    tint = if (attempt.passed) EmeraldCorrect else SunnyGold,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = if (attempt.passed) "Congratulations! You Passed!" else "Good Try! Keep Practicing",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = attempt.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ResultStatPill("Score", "${attempt.correctAnswers}/${attempt.totalQuestions}")
                    ResultStatPill("Accuracy", "$percent%")
                    ResultStatPill("Coins", "+${attempt.coinsEarned}")
                }

                val isMonthlyTest = attempt.testType == TestType.MONTHLY.name
                val earnsCertificate = attempt.passed && isMonthlyTest

                if (earnsCertificate) {
                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = { showCertificate = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("view_earned_certificate_button")
                    ) {
                        Icon(Icons.Filled.Verified, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("View Pass Certificate")
                    }
                } else if (attempt.passed) {
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Great job! Pass the Monthly Grand Quiz (60%+) or reach 10M coins to earn a certificate.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = if (isMonthlyTest) {
                            "Score 60% or higher in the Monthly Grand Quiz to unlock the Pass Certificate and full coin reward!"
                        } else {
                            "Score 60% or higher to earn the full coin reward! Certificates unlock on Monthly Grand Quiz and 10M coins."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("result_done_button")
                ) {
                    Text("Back to Home")
                }
            }
        }
    }

    if (showCertificate) {
        CertificateDialog(
            studentName = attempt.studentName,
            schoolName = attempt.schoolName,
            certificateTitle = "${attempt.title} Pass Certificate",
            achievementLine = "Successfully passed with ${attempt.correctAnswers}/${attempt.totalQuestions} ($percent% accuracy)",
            dateText = formatAttemptDate(attempt),
            onDismiss = { showCertificate = false }
        )
    }
}

@Composable
private fun ResultStatPill(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SunnyGoldLight
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = OnSunnyGold)
            Text(
                text = value,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                color = OnSunnyGold
            )
        }
    }
}
