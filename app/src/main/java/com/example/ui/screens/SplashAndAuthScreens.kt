package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.GradeCatalog
import com.example.data.repository.GoogleSignInButton
import com.example.data.repository.uriToCompressedBase64
import com.example.ui.components.AvatarImage
import com.example.ui.theme.CobaltDark
import com.example.ui.theme.CobaltPrimary
import com.example.ui.theme.SunnyGold

@Composable
fun SplashLogoScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(CobaltPrimary, CobaltDark)
                )
            )
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_launcher_fg),
                contentDescription = "BrightQuiz Logo",
                modifier = Modifier
                    .size(144.dp)
                    .clip(RoundedCornerShape(36.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "BrightQuiz",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Class-Wise GK, Subjects & Gemini Study Tutor",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFDBE7FF),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(
                color = SunnyGold,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun GoogleAuthScreen(
    onAuthSuccess: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_gk),
                    contentDescription = "Children exploring GK and space",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Welcome to BrightQuiz!",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sign in to register your student profile, track coins & levels, compete on the leaderboard, and unlock certificates.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                GoogleSignInButton(
                    onAuthSuccess = {
                        errorMessage = null
                        onAuthSuccess()
                    },
                    onAuthError = { msg -> errorMessage = msg },
                    onAuthCancelled = { msg -> errorMessage = msg }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudentRegistrationWizard(
    defaultEmail: String,
    defaultName: String,
    isBusy: Boolean,
    errorBanner: String?,
    onSubmitRegistration: (
        country: String,
        phone: String,
        email: String,
        studentName: String,
        grade: String,
        schoolName: String,
        studyPasscode: String,
        avatarBase64: String?
    ) -> Unit
) {
    val context = LocalContext.current
    var step by remember { mutableIntStateOf(1) }
    val totalSteps = 6

    var country by remember { mutableStateOf("India") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(defaultEmail) }
    var studyPassword by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf(defaultName) }
    var selectedGrade by remember { mutableStateOf("") }
    var schoolName by remember { mutableStateOf("") }
    var selectedAvatarBase64 by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val compressed = uriToCompressedBase64(context, uri, maxDimension = 320)
            if (compressed != null) {
                selectedAvatarBase64 = compressed
                validationError = null
            } else {
                validationError = "Could not load selected image."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Student Registration",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Step $step of $totalSteps",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { step / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                AnimatedContent(targetState = step, label = "reg_step") { currentStep ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        when (currentStep) {
                            1 -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Public, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Country & Phone Number", style = MaterialTheme.typography.headlineMedium)
                                }
                                Spacer(Modifier.height(12.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("India", "United States", "United Kingdom", "Canada", "UAE", "Singapore").forEach { item ->
                                        FilterChip(
                                            selected = country == item,
                                            onClick = { country = item },
                                            label = { Text(item) }
                                        )
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = country,
                                    onValueChange = { country = it },
                                    label = { Text("Country") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reg_country_input")
                                )
                                Spacer(Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    label = { Text("Parent / Student Phone Number") },
                                    placeholder = { Text("+91 9876543210") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reg_phone_input")
                                )
                            }

                            2 -> {
                                Text("Email & Study Password", style = MaterialTheme.typography.headlineMedium)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Your Google account secures your login. Set a student contact email and a personal study passcode.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(14.dp))
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email Address") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reg_email_input")
                                )
                                Spacer(Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = studyPassword,
                                    onValueChange = { studyPassword = it },
                                    label = { Text("Study Passcode / Password") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reg_password_input")
                                )
                            }

                            3 -> {
                                Text("Student's Name", style = MaterialTheme.typography.headlineMedium)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "This name will appear on your Leaderboard badge and Certificates!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(14.dp))
                                OutlinedTextField(
                                    value = studentName,
                                    onValueChange = { studentName = it },
                                    label = { Text("Full Student Name") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reg_student_name_input")
                                )
                            }

                            4 -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Select Your Class / Grade", style = MaterialTheme.typography.headlineMedium)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Quiz questions and your Gemini Study Tutor will be matched to your class level.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(14.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    GradeCatalog.grades.forEach { gradeOption ->
                                        FilterChip(
                                            selected = selectedGrade == gradeOption,
                                            onClick = {
                                                selectedGrade = gradeOption
                                                validationError = null
                                            },
                                            label = { Text(gradeOption) },
                                            modifier = Modifier.testTag(
                                                "reg_grade_${gradeOption.lowercase().replace(" ", "_")}"
                                            )
                                        )
                                    }
                                }
                            }

                            5 -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(8.dp))
                                    Text("School Name", style = MaterialTheme.typography.headlineMedium)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Enter your school name so classmates and friends can recognize you.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(14.dp))
                                OutlinedTextField(
                                    value = schoolName,
                                    onValueChange = { schoolName = it },
                                    label = { Text("School Name") },
                                    placeholder = { Text("e.g., Sunrise Public School") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reg_school_name_input")
                                )
                            }

                            else -> {
                                Text("Choose Profile Picture", style = MaterialTheme.typography.headlineMedium)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Pick a photo from your device gallery, or continue with your student initial badge.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(16.dp))
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AvatarImage(
                                        avatarBase64 = selectedAvatarBase64,
                                        studentName = studentName,
                                        size = 108.dp
                                    )
                                }
                                Spacer(Modifier.height(18.dp))

                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("reg_gallery_avatar_button")
                                ) {
                                    Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Pick Picture from Gallery")
                                }

                                if (selectedAvatarBase64 != null) {
                                    Spacer(Modifier.height(8.dp))
                                    TextButton(
                                        onClick = { selectedAvatarBase64 = null },
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    ) {
                                        Text("Use Initial Badge Instead")
                                    }
                                }
                            }
                        }
                    }
                }

                val displayError = validationError ?: errorBanner
                if (displayError != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = displayError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        TextButton(
                            onClick = {
                                validationError = null
                                step -= 1
                            },
                            modifier = Modifier.testTag("reg_back_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Back")
                        }
                    } else {
                        Spacer(Modifier.width(8.dp))
                    }

                    Button(
                        onClick = {
                            validationError = null
                            when (step) {
                                1 -> {
                                    if (country.trim().length < 2) {
                                        validationError = "Please enter your country."
                                    } else if (phone.trim().length < 5) {
                                        validationError = "Please enter a valid phone number."
                                    } else step = 2
                                }
                                2 -> {
                                    if (!email.contains("@") || email.trim().length < 5) {
                                        validationError = "Please enter a valid email address."
                                    } else if (studyPassword.trim().length < 4) {
                                        validationError = "Please enter at least 4 characters for your passcode."
                                    } else step = 3
                                }
                                3 -> {
                                    if (studentName.trim().isEmpty()) {
                                        validationError = "Please enter the student's name."
                                    } else step = 4
                                }
                                4 -> {
                                    if (selectedGrade.isBlank()) {
                                        validationError = "Please select your class / grade."
                                    } else step = 5
                                }
                                5 -> {
                                    if (schoolName.trim().isEmpty()) {
                                        validationError = "Please enter your school name."
                                    } else step = 6
                                }
                                6 -> {
                                    onSubmitRegistration(
                                        country,
                                        phone,
                                        email,
                                        studentName,
                                        selectedGrade,
                                        schoolName,
                                        studyPassword,
                                        selectedAvatarBase64
                                    )
                                }
                            }
                        },
                        enabled = !isBusy,
                        modifier = Modifier.testTag("reg_next_button")
                    ) {
                        if (isBusy) {
                            CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(if (step == totalSteps) "Finish & Enter App" else "Next")
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                imageVector = if (step == totalSteps) Icons.Filled.Check else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}
