package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.LevelsCatalog
import com.example.data.model.TestAttempt
import com.example.data.model.UiState
import com.example.data.model.UserProfile
import com.example.ui.components.CertificateDialog
import com.example.ui.components.formatAttemptDate
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.OnCoralContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.OnSunnyGold
import com.example.ui.theme.SunnyGold
import com.example.ui.theme.SunnyGoldLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    profile: UserProfile,
    historyState: UiState<List<TestAttempt>>
) {
    var selectedAttemptForCert by remember { mutableStateOf<TestAttempt?>(null) }
    var showMasterCert by remember { mutableStateOf(false) }

    val isMaster = profile.coins >= LevelsCatalog.MASTER_COINS

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Being Master Achievement Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isMaster) SunnyGoldLight else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = if (isMaster) 2.dp else 1.dp,
                    color = if (isMaster) SunnyGold else MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = SunnyGold,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Level 10: Being Master Certificate",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isMaster) OnSunnyGold else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isMaster) {
                                "Unlocked at 10 Lakh coins! View your official Mastery Certificate."
                            } else {
                                "Reach 10,00,000 (10 Lakh) coins to unlock the Being Master Certificate."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isMaster) OnSunnyGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isMaster) {
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = { showMasterCert = true },
                            modifier = Modifier.testTag("history_master_cert_button")
                        ) {
                            Text("View")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Your Tests & Certificates",
                style = MaterialTheme.typography.titleLarge
            )
        }

        when (historyState) {
            is UiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is UiState.Error -> {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CoralContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = historyState.message,
                            color = OnCoralContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            is UiState.Success -> {
                val attempts = historyState.data
                if (attempts.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(10.dp))
                                Text("No tests completed yet", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Complete a Daily, Weekly, Monthly, or Practice quiz on the Home tab to see your score and certificates here.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(attempts, key = { it.id.ifBlank { "${it.title}_${it.createdAt}" } }) { attempt ->
                        val pct = if (attempt.totalQuestions > 0) {
                            (attempt.correctAnswers * 100) / attempt.totalQuestions
                        } else 0
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(attempt.title, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            text = formatAttemptDate(attempt),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = if (attempt.passed) EmeraldContainer else CoralContainer
                                    ) {
                                        Text(
                                            text = if (attempt.passed) "PASSED" else "TRY AGAIN",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (attempt.passed) OnEmeraldContainer else OnCoralContainer,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Score: ${attempt.correctAnswers}/${attempt.totalQuestions} ($pct%) • +${attempt.coinsEarned} coins",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (attempt.passed) {
                                        OutlinedButton(
                                            onClick = { selectedAttemptForCert = attempt },
                                            modifier = Modifier.testTag("history_view_cert_${attempt.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Verified,
                                                contentDescription = null,
                                                tint = EmeraldCorrect,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(6.dp))
                                            Text("Certificate")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val certAttempt = selectedAttemptForCert
    if (certAttempt != null) {
        val pct = if (certAttempt.totalQuestions > 0) {
            (certAttempt.correctAnswers * 100) / certAttempt.totalQuestions
        } else 0
        CertificateDialog(
            studentName = certAttempt.studentName,
            schoolName = certAttempt.schoolName,
            certificateTitle = "${certAttempt.title} Pass Certificate",
            achievementLine = "Passed with ${certAttempt.correctAnswers}/${certAttempt.totalQuestions} ($pct% accuracy)",
            dateText = formatAttemptDate(certAttempt),
            onDismiss = { selectedAttemptForCert = null }
        )
    }

    if (showMasterCert) {
        val today = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())
        CertificateDialog(
            studentName = profile.studentName,
            schoolName = profile.schoolName,
            certificateTitle = "Level 10: Being Master Achievement",
            achievementLine = "Achieved 10,00,000+ Coins — Grand GK & Current Affairs Master!",
            dateText = today,
            onDismiss = { showMasterCert = false }
        )
    }
}
