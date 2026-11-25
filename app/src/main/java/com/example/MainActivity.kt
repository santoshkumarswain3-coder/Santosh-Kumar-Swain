package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.firebase.configureAppCheck
import com.example.data.model.UiState
import com.example.data.model.UserProfile
import com.example.data.repository.attemptAutoSignIn
import com.example.data.repository.signOutUser
import com.example.ui.components.BrightQuizTopBar
import com.example.ui.components.CertificateDialog
import com.example.ui.components.LevelRoadmapDialog
import com.example.ui.screens.AskAiScreen
import com.example.ui.screens.GoogleAuthScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileSocialScreen
import com.example.ui.screens.SplashLogoScreen
import com.example.ui.screens.StudentRegistrationWizard
import com.example.ui.screens.TestRunnerScreen
import com.example.ui.theme.BrightQuizTheme
import com.example.ui.viewmodel.MainViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mandatory Step 1: Configure App Check with launch intent before setContent / ViewModels
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            BrightQuizTheme {
                BrightQuizAppRoot()
            }
        }
    }
}

@Composable
fun BrightQuizAppRoot() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var showSplash by remember { mutableStateOf(true) }
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose { Firebase.auth.removeAuthStateListener(listener) }
    }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { currentUser = Firebase.auth.currentUser },
            onUnauthenticated = {},
            scope = scope
        )
        delay(3000L)
        showSplash = false
    }

    if (showSplash) {
        SplashLogoScreen()
        return
    }

    val user = currentUser
    if (user == null) {
        GoogleAuthScreen(
            onAuthSuccess = { currentUser = Firebase.auth.currentUser }
        )
    } else {
        key(user.uid) {
            AuthenticatedShell(
                firebaseUser = user,
                onSignOut = {
                    signOutUser(credentialManager, scope) {
                        currentUser = null
                    }
                }
            )
        }
    }
}

@Composable
private fun AuthenticatedShell(
    firebaseUser: FirebaseUser,
    onSignOut: () -> Unit
) {
    val viewModel: MainViewModel = viewModel(
        key = firebaseUser.uid,
        factory = MainViewModel.provideFactory(firebaseUser.uid)
    )

    val profileState by viewModel.userProfileState.collectAsStateWithLifecycle()
    val leaderboardState by viewModel.leaderboardState.collectAsStateWithLifecycle()
    val historyState by viewModel.historyState.collectAsStateWithLifecycle()
    val friendRequestsState by viewModel.friendRequestsState.collectAsStateWithLifecycle()
    val allQuestions by viewModel.allQuestions.collectAsStateWithLifecycle()
    val activeTest by viewModel.activeTest.collectAsStateWithLifecycle()
    val askState by viewModel.askState.collectAsStateWithLifecycle()
    val aiImageState by viewModel.aiImageState.collectAsStateWithLifecycle()
    val isBusy by viewModel.isBusy.collectAsStateWithLifecycle()
    val actionFeedback by viewModel.actionFeedback.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(actionFeedback) {
        val msg = actionFeedback ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearActionFeedback()
    }

    when (val pState = profileState) {
        is UiState.Loading -> {
            androidx.compose.material3.Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = "Loading your student profile...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = pState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(14.dp))
                    Button(onClick = onSignOut) {
                        Text("Sign Out & Retry")
                    }
                }
            }
        }

        is UiState.Success -> {
            val profile = pState.data
            if (profile == null) {
                StudentRegistrationWizard(
                    defaultEmail = firebaseUser.email.orEmpty(),
                    defaultName = firebaseUser.displayName.orEmpty(),
                    aiImageState = aiImageState,
                    isBusy = isBusy,
                    errorBanner = actionFeedback,
                    onGenerateAiAvatar = viewModel::generateAiAvatar,
                    onResetAiImage = viewModel::resetAiImageState,
                    onSubmitRegistration = viewModel::completeRegistration
                )
            } else {
                MainAuthorizedTabs(
                    profile = profile,
                    viewModel = viewModel,
                    leaderboardState = leaderboardState,
                    historyState = historyState,
                    friendRequestsState = friendRequestsState,
                    allQuestions = allQuestions,
                    activeTest = activeTest,
                    askState = askState,
                    aiImageState = aiImageState,
                    isBusy = isBusy,
                    snackbarHostState = snackbarHostState,
                    onSignOut = onSignOut
                )
            }
        }
    }
}

@Composable
private fun MainAuthorizedTabs(
    profile: UserProfile,
    viewModel: MainViewModel,
    leaderboardState: UiState<List<com.example.data.model.LeaderboardEntry>>,
    historyState: UiState<List<com.example.data.model.TestAttempt>>,
    friendRequestsState: UiState<List<com.example.data.model.FriendRequest>>,
    allQuestions: List<com.example.data.model.QuizQuestion>,
    activeTest: com.example.ui.viewmodel.ActiveTestSession?,
    askState: com.example.ui.viewmodel.AskTabState,
    aiImageState: com.example.ui.viewmodel.AiImageState,
    isBusy: Boolean,
    snackbarHostState: SnackbarHostState,
    onSignOut: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showProfileScreen by remember { mutableStateOf(false) }
    var profileInitialTab by remember { mutableIntStateOf(0) }
    var showLevelsDialog by remember { mutableStateOf(false) }
    var showMasterCertDialog by remember { mutableStateOf(false) }

    // Full-screen Test Runner takes precedence
    if (activeTest != null) {
        TestRunnerScreen(
            session = activeTest,
            onSelectOption = viewModel::selectAnswerOption,
            onNextOrFinish = viewModel::advanceOrFinishTest,
            onCloseResult = viewModel::closeActiveTest
        )
        return
    }

    if (showProfileScreen) {
        ProfileSocialScreen(
            profile = profile,
            leaderboardState = leaderboardState,
            friendRequestsState = friendRequestsState,
            aiImageState = aiImageState,
            isBusy = isBusy,
            initialTab = profileInitialTab,
            onBack = { showProfileScreen = false },
            onUpdateProfile = viewModel::updateProfile,
            onGenerateAiAvatar = viewModel::generateAiAvatar,
            onResetAiImage = viewModel::resetAiImageState,
            onSendFriendRequest = viewModel::sendFriendRequest,
            onRespondFriendRequest = viewModel::respondToFriendRequest,
            onSignOut = onSignOut
        )
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            BrightQuizTopBar(
                profile = profile,
                onCoinsClick = { showLevelsDialog = true },
                onLevelClick = { showLevelsDialog = true },
                onProfileClick = {
                    profileInitialTab = 0
                    viewModel.resetAiImageState()
                    showProfileScreen = true
                }
            )
        },
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home") },
                    modifier = Modifier.testTag("bottom_nav_home")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "Ask"
                        )
                    },
                    label = { Text("Ask") },
                    modifier = Modifier.testTag("bottom_nav_ask")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = "History"
                        )
                    },
                    label = { Text("History") },
                    modifier = Modifier.testTag("bottom_nav_history")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    profile = profile,
                    allQuestions = allQuestions,
                    aiImageState = aiImageState,
                    isBusy = isBusy,
                    onStartTest = viewModel::startTest,
                    onGenerateQuestionImage = viewModel::generateCurrentAffairsImage,
                    onResetAiImage = viewModel::resetAiImageState,
                    onUploadQuestion = { category, qText, opts, correctIdx, explanation, imgB64, onDone ->
                        viewModel.uploadCustomQuestion(
                            authorName = profile.studentName,
                            category = category,
                            questionText = qText,
                            options = opts,
                            correctIndex = correctIdx,
                            explanation = explanation,
                            imageBase64 = imgB64,
                            onSuccess = onDone
                        )
                    }
                )
                1 -> AskAiScreen(
                    profile = profile,
                    askState = askState,
                    onSendQuestion = viewModel::askStudyQuestion
                )
                else -> HistoryScreen(
                    profile = profile,
                    historyState = historyState
                )
            }
        }
    }

    if (showLevelsDialog) {
        LevelRoadmapDialog(
            coins = profile.coins,
            onOpenMasterCertificate = {
                showLevelsDialog = false
                showMasterCertDialog = true
            },
            onDismiss = { showLevelsDialog = false }
        )
    }

    if (showMasterCertDialog) {
        val today = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())
        CertificateDialog(
            studentName = profile.studentName,
            schoolName = profile.schoolName,
            certificateTitle = "Level 10: Being Master Achievement",
            achievementLine = "Achieved 10,00,000+ Coins — Grand GK & Current Affairs Master!",
            dateText = today,
            onDismiss = { showMasterCertDialog = false }
        )
    }
}
