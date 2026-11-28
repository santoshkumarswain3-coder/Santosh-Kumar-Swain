package com.example.ui.components

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.LevelsCatalog
import com.example.data.model.TestAttempt
import com.example.data.model.UserProfile
import com.example.data.repository.base64ToBitmap
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.OnSunnyGold
import com.example.ui.theme.SunnyGold
import com.example.ui.theme.SunnyGoldLight
import com.example.ui.theme.WarmIvory
import com.example.util.CertificateImageSaver
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AvatarImage(
    avatarBase64: String?,
    studentName: String,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(avatarBase64) { base64ToBitmap(avatarBase64) }
    val initial = studentName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "S"
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "$studentName profile photo",
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (studentName.isBlank()) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Student avatar",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.55f)
                )
            } else {
                Text(
                    text = initial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.42f).sp
                )
            }
        }
    }
}

@Composable
fun BrightQuizTopBar(
    profile: UserProfile,
    profileLabel: String = "Profile",
    onCoinsClick: () -> Unit,
    onLevelClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val level = LevelsCatalog.levelForCoins(profile.coins)
    val levelTitle = LevelsCatalog.displayTitleWithMilestone(profile.coins)
    val formattedCoins = remember(profile.coins) {
        NumberFormat.getNumberInstance(Locale.US).format(profile.coins)
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Option 1: Coins Pill
            Surface(
                shape = RoundedCornerShape(50),
                color = SunnyGoldLight,
                border = BorderStroke(1.dp, SunnyGold.copy(alpha = 0.6f)),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onCoinsClick)
                    .testTag("topbar_coins_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.MonetizationOn,
                        contentDescription = "Coins",
                        tint = SunnyGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formattedCoins,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = OnSunnyGold,
                        maxLines = 1
                    )
                }
            }

            // Option 2: Level Badge Pill
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 8.dp)
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onLevelClick)
                    .testTag("topbar_level_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.MilitaryTech,
                        contentDescription = "Level",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Lv ${level.levelNumber} • $levelTitle",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Option 3: Profile Button
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onProfileClick)
                    .testTag("topbar_profile_button")
            ) {
                Row(
                    modifier = Modifier.padding(start = 4.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarImage(
                        avatarBase64 = profile.avatarBase64,
                        studentName = profile.studentName,
                        size = 34.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = profileLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun LevelRoadmapDialog(
    coins: Long,
    onOpenMasterCertificate: () -> Unit,
    onDismiss: () -> Unit
) {
    val currentLevel = LevelsCatalog.levelForCoins(coins)
    val next = LevelsCatalog.nextLevel(coins)
    val formattedCoins = NumberFormat.getNumberInstance(Locale.US).format(coins)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = SunnyGold,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Levels & Milestones",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Total Coins: $formattedCoins",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (next != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    val span = (next.minCoins - currentLevel.minCoins).coerceAtLeast(1L)
                    val prog = ((coins - currentLevel.minCoins).toFloat() / span.toFloat()).coerceIn(0f, 1f)
                    Text(
                        text = "Next: Lv ${next.levelNumber} ${next.title} (${NumberFormat.getNumberInstance(Locale.US).format(next.minCoins)} coins)",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { prog },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }

                if (coins >= LevelsCatalog.MASTER_COINS) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onOpenMasterCertificate,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_master_certificate_button")
                    ) {
                        Icon(Icons.Filled.Verified, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("View Being Master Certificate")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                LevelsCatalog.tiers.drop(1).forEach { tier ->
                    val unlocked = coins >= tier.minCoins
                    val isCurrent = currentLevel.levelNumber == tier.levelNumber
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = when {
                            isCurrent -> MaterialTheme.colorScheme.primaryContainer
                            unlocked -> MaterialTheme.colorScheme.surfaceVariant
                            else -> MaterialTheme.colorScheme.surface
                        },
                        border = if (!unlocked) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Level ${tier.levelNumber}: ${tier.title}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (unlocked) "Unlocked" else "${NumberFormat.getNumberInstance(Locale.US).format(tier.minCoins)} c",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (unlocked) EmeraldCorrect else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = tier.badgeNote,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (tier.levelNumber == 5) {
                                val wowPlusUnlocked = coins >= LevelsCatalog.WOW_VERY_NICE_COINS
                                Text(
                                    text = if (wowPlusUnlocked) "40k Milestone Unlocked: WOW Very nice!" else "Bonus at 40,000 coins: WOW Very nice",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (wowPlusUnlocked) EmeraldCorrect else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.End)
                        .testTag("close_levels_dialog")
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun CertificateDialog(
    studentName: String,
    schoolName: String,
    certificateTitle: String,
    achievementLine: String,
    dateText: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    var saveFeedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/png")
    ) { uri ->
        if (uri == null) {
            isSaving = false
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val res = CertificateImageSaver.writeCertificatePngToUri(
                context = context,
                targetUri = uri,
                studentName = studentName,
                schoolName = schoolName,
                certificateTitle = certificateTitle,
                achievementLine = achievementLine,
                dateText = dateText
            )
            isSaving = false
            res.onSuccess {
                saveFeedback = true to "Certificate saved!"
                Toast.makeText(context, "Certificate saved!", Toast.LENGTH_SHORT).show()
            }.onFailure { err ->
                saveFeedback = false to (err.localizedMessage ?: "Failed to save certificate.")
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = WarmIvory),
            border = BorderStroke(3.dp, SunnyGold),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_certificate_seal),
                    contentDescription = "Gold Award Seal",
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "CERTIFICATE OF ACHIEVEMENT",
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSunnyGold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = certificateTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Proudly awarded to",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF475569)
                )
                Text(
                    text = studentName,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF1F6AEA),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = schoolName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SunnyGoldLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = achievementLine,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSunnyGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Issued on $dateText • BrightQuiz Academy",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF64748B)
                )

                val feedback = saveFeedback
                if (feedback != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = feedback.second,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (feedback.first) EmeraldCorrect else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (isSaving) return@Button
                        saveFeedback = null
                        isSaving = true
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            scope.launch {
                                val res = CertificateImageSaver.saveCertificateToGallery(
                                    context = context,
                                    studentName = studentName,
                                    schoolName = schoolName,
                                    certificateTitle = certificateTitle,
                                    achievementLine = achievementLine,
                                    dateText = dateText
                                )
                                isSaving = false
                                res.onSuccess {
                                    saveFeedback = true to "Saved to Gallery (Pictures/BrightQuiz)!"
                                    Toast.makeText(
                                        context,
                                        "Certificate saved to Gallery!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }.onFailure { err ->
                                    saveFeedback = false to (err.localizedMessage ?: "Failed to save to gallery.")
                                }
                            }
                        } else {
                            createDocumentLauncher.launch("BrightQuiz_Certificate_${System.currentTimeMillis()}.png")
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_certificate_gallery_button")
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saving...")
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = "Save certificate to gallery"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save to Gallery")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("close_certificate_dialog")
                ) {
                    Text("Done")
                }
            }
        }
    }
}

fun formatAttemptDate(attempt: TestAttempt): String {
    val date = attempt.createdAt?.toDate() ?: Date()
    return SimpleDateFormat("MMM dd, yyyy", Locale.US).format(date)
}
