package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.GradeCatalog
import com.example.data.model.QuestionCategory
import com.example.data.model.QuizQuestion
import com.example.data.model.TestType
import com.example.data.model.UserProfile
import com.example.data.repository.base64ToBitmap
import com.example.data.repository.uriToCompressedBase64
import com.example.ui.theme.CobaltPrimary
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.OnSunnyGold
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.SunnyGold
import com.example.ui.theme.SunnyGoldLight
import com.example.util.AppStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    profile: UserProfile,
    allQuestions: List<QuizQuestion>,
    isBusy: Boolean,
    strings: AppStrings = AppStrings.forLanguage(profile.language),
    onStartTest: (TestType, QuestionCategory?) -> Unit,
    onUploadQuestion: (
        category: QuestionCategory,
        targetGrade: Int,
        questionText: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String,
        imageBase64: String?,
        onDone: () -> Unit
    ) -> Unit
) {
    var showUploadDialog by remember { mutableStateOf(false) }
    val userGrade = GradeCatalog.normalizeGrade(profile.grade)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_gk),
                        contentDescription = "GK and Subject Quiz Hero Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(172.dp),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(172.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.15f),
                                        Color.Black.copy(alpha = 0.72f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = SunnyGold
                        ) {
                            Text(
                                text = "$userGrade • ${allQuestions.size} Fresh MCQs Ready",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnSunnyGold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Hi ${profile.studentName}!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Unique $userGrade questions in every test • Win Monthly Pass Certificates.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }

        // Timed Tests Section
        item {
            Text(
                text = strings.timedTestsHeader,
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TestModeCard(
                    title = TestType.DAILY.title,
                    subtitle = TestType.DAILY.subtitle,
                    badgeText = "+10 Coins",
                    icon = Icons.Filled.Bolt,
                    accentColor = CobaltPrimary,
                    tag = "start_daily_test_button",
                    onClick = { onStartTest(TestType.DAILY, null) }
                )
                TestModeCard(
                    title = TestType.WEEKLY.title,
                    subtitle = TestType.WEEKLY.subtitle,
                    badgeText = "+100 Coins",
                    icon = Icons.Filled.CalendarViewWeek,
                    accentColor = SkyAccent,
                    tag = "start_weekly_test_button",
                    onClick = { onStartTest(TestType.WEEKLY, null) }
                )
                TestModeCard(
                    title = TestType.MONTHLY.title,
                    subtitle = TestType.MONTHLY.subtitle,
                    badgeText = "+1,000 Coins",
                    icon = Icons.Filled.CalendarMonth,
                    accentColor = EmeraldCorrect,
                    tag = "start_monthly_test_button",
                    onClick = { onStartTest(TestType.MONTHLY, null) }
                )
            }
        }

        // Practice by Subject & Pictures
        item {
            Text(
                text = strings.subjectsHeader,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_current_affairs_space),
                            contentDescription = "Space Current Affairs",
                            modifier = Modifier
                                .weight(1f)
                                .height(92.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Image(
                            painter = painterResource(id = R.drawable.img_current_affairs_nature),
                            contentDescription = "Environment Current Affairs",
                            modifier = Modifier
                                .weight(1f)
                                .height(92.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = strings.subjectsSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onStartTest(TestType.PRACTICE, null) },
                            modifier = Modifier.testTag("start_practice_all_button")
                        ) {
                            Icon(Icons.Filled.Newspaper, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("All Subjects & Pictures")
                        }
                        QuestionCategory.entries.forEach { category ->
                            OutlinedButton(
                                onClick = { onStartTest(TestType.PRACTICE, category) },
                                modifier = Modifier.testTag("practice_${category.name.lowercase()}")
                            ) {
                                Icon(
                                    imageVector = subjectIcon(category),
                                    contentDescription = null,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(category.displayName)
                            }
                        }
                    }
                }
            }
        }

        // Upload MCQ & Picture Question Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text(strings.uploadHeader, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Add new multiple choice questions and subject pictures for students to practice.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = { showUploadDialog = true },
                        modifier = Modifier.testTag("open_upload_question_button")
                    ) {
                        Icon(Icons.Filled.AddCircle, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Add MCQ")
                    }
                }
            }
        }
    }

    if (showUploadDialog) {
        UploadQuestionDialog(
            isBusy = isBusy,
            defaultGrade = GradeCatalog.parseGradeNumber(profile.grade),
            onDismiss = { showUploadDialog = false },
            onSubmit = { category, targetGrade, qText, options, correctIdx, explanation, imgBase64 ->
                onUploadQuestion(category, targetGrade, qText, options, correctIdx, explanation, imgBase64) {
                    showUploadDialog = false
                }
            }
        )
    }
}

private fun subjectIcon(category: QuestionCategory): ImageVector = when (category) {
    QuestionCategory.CURRENT_AFFAIRS -> Icons.Filled.Newspaper
    QuestionCategory.SCIENCE_SPACE -> Icons.Filled.Science
    QuestionCategory.MATHEMATICS -> Icons.Filled.Calculate
    QuestionCategory.ENGLISH_LITERATURE -> Icons.Filled.MenuBook
    QuestionCategory.GEOGRAPHY -> Icons.Filled.Public
    QuestionCategory.HISTORY_CIVICS -> Icons.Filled.HistoryEdu
    QuestionCategory.COMPUTER_TECH -> Icons.Filled.Computer
    QuestionCategory.ENVIRONMENT_NATURE -> Icons.Filled.Eco
    QuestionCategory.SPORTS_ARTS -> Icons.Filled.SportsSoccer
    QuestionCategory.LOGICAL_REASONING -> Icons.Filled.Psychology
    QuestionCategory.GENERAL -> Icons.Filled.Lightbulb
}

@Composable
private fun TestModeCard(
    title: String,
    subtitle: String,
    badgeText: String,
    icon: ImageVector,
    accentColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = accentColor.copy(alpha = 0.14f),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = SunnyGoldLight
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSunnyGold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Start $title",
                    tint = accentColor
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UploadQuestionDialog(
    isBusy: Boolean,
    defaultGrade: Int,
    onDismiss: () -> Unit,
    onSubmit: (
        category: QuestionCategory,
        targetGrade: Int,
        questionText: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String,
        imageBase64: String?
    ) -> Unit
) {
    val context = LocalContext.current
    var category by remember { mutableStateOf(QuestionCategory.CURRENT_AFFAIRS) }
    var targetGrade by remember { mutableIntStateOf(defaultGrade) }
    var questionText by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctIdx by remember { mutableIntStateOf(0) }
    var explanation by remember { mutableStateOf("") }
    var imageBase64 by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imageBase64 = uriToCompressedBase64(context, uri, maxDimension = 420)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Upload Subject / GK MCQ", style = MaterialTheme.typography.headlineMedium)

                Text("Subject", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuestionCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.displayName) }
                        )
                    }
                }

                Text("Target Class", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = targetGrade == 0,
                        onClick = { targetGrade = 0 },
                        label = { Text("All Classes") }
                    )
                    (1..12).forEach { g ->
                        FilterChip(
                            selected = targetGrade == g,
                            onClick = { targetGrade = g },
                            label = { Text("Gr $g") }
                        )
                    }
                }

                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Question") },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_question_input")
                )

                listOf(
                    0 to Pair(optA) { v: String -> optA = v },
                    1 to Pair(optB) { v: String -> optB = v },
                    2 to Pair(optC) { v: String -> optC = v },
                    3 to Pair(optD) { v: String -> optD = v }
                ).forEach { (idx, pair) ->
                    val (value, setter) = pair
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = correctIdx == idx,
                            onClick = { correctIdx = idx },
                            modifier = Modifier.testTag("upload_correct_radio_$idx")
                        )
                        OutlinedTextField(
                            value = value,
                            onValueChange = setter,
                            label = { Text("Option ${'A' + idx}${if (correctIdx == idx) " (Correct)" else ""}") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("upload_option_input_$idx")
                        )
                    }
                }

                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = { Text("Short Explanation (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Optional Subject Picture", style = MaterialTheme.typography.titleMedium)
                val previewBmp = remember(imageBase64) { base64ToBitmap(imageBase64) }
                if (previewBmp != null) {
                    Image(
                        bitmap = previewBmp.asImageBitmap(),
                        contentDescription = "Question image preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                OutlinedButton(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("upload_gallery_image_button")
                ) {
                    Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Pick Picture from Gallery")
                }

                if (errorMsg != null) {
                    Text(
                        text = errorMsg ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val opts = listOf(optA.trim(), optB.trim(), optC.trim(), optD.trim())
                            if (questionText.trim().length < 5) {
                                errorMsg = "Enter a question (at least 5 characters)."
                            } else if (opts.any { it.isEmpty() }) {
                                errorMsg = "Please fill in all 4 options."
                            } else {
                                errorMsg = null
                                onSubmit(category, targetGrade, questionText, opts, correctIdx, explanation, imageBase64)
                            }
                        },
                        enabled = !isBusy,
                        modifier = Modifier.testTag("submit_uploaded_question_button")
                    ) {
                        Text(if (isBusy) "Saving..." else "Publish MCQ")
                    }
                }
            }
        }
    }
}
