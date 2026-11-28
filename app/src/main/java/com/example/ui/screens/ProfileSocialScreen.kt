package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.FriendRequest
import com.example.data.model.GradeCatalog
import com.example.data.model.LeaderboardEntry
import com.example.data.model.LevelsCatalog
import com.example.data.model.UiState
import com.example.data.model.UserProfile
import com.example.data.repository.uriToCompressedBase64
import com.example.ui.components.AvatarImage
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.OnSunnyGold
import com.example.ui.theme.SunnyGold
import com.example.ui.theme.SunnyGoldLight
import com.example.util.AppStrings
import com.example.util.LanguageCatalog
import java.text.NumberFormat
import java.util.Locale

private const val CUSTOMER_CARE_NUMBER = "8018026145"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSocialScreen(
    profile: UserProfile,
    leaderboardState: UiState<List<LeaderboardEntry>>,
    friendRequestsState: UiState<List<FriendRequest>>,
    isBusy: Boolean,
    initialTab: Int = 0,
    strings: AppStrings = AppStrings.forLanguage(profile.language),
    onBack: () -> Unit,
    onUpdateProfile: (
        name: String,
        school: String,
        grade: String,
        language: String,
        avatarBase64: String?
    ) -> Unit,
    onSendFriendRequest: (LeaderboardEntry) -> Unit,
    onRespondFriendRequest: (String, Boolean) -> Unit,
    onSignOut: () -> Unit
) {
    BackHandler(onBack = onBack)

    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }
    val tabs = listOf(strings.tabMyProfile, strings.tabLeaderboard, strings.tabFriends)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        // Header Bar
        Surface(tonalElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("profile_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = strings.profileScreenTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        PrimaryTabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = { Text(title) },
                    modifier = Modifier.testTag("profile_tab_$idx")
                )
            }
        }

        when (selectedTab) {
            0 -> MyProfileTab(
                profile = profile,
                strings = strings,
                isBusy = isBusy,
                onUpdateProfile = onUpdateProfile,
                onSignOut = onSignOut
            )
            1 -> LeaderboardTab(
                currentUid = profile.userId,
                leaderboardState = leaderboardState
            )
            else -> FriendsTab(
                currentUid = profile.userId,
                leaderboardState = leaderboardState,
                friendRequestsState = friendRequestsState,
                onSendFriendRequest = onSendFriendRequest,
                onRespondFriendRequest = onRespondFriendRequest
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MyProfileTab(
    profile: UserProfile,
    strings: AppStrings,
    isBusy: Boolean,
    onUpdateProfile: (
        name: String,
        school: String,
        grade: String,
        language: String,
        avatarBase64: String?
    ) -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var editingName by remember(profile.studentName) { mutableStateOf(profile.studentName) }
    var editingSchool by remember(profile.schoolName) { mutableStateOf(profile.schoolName) }
    var editingGrade by remember(profile.grade) { mutableStateOf(GradeCatalog.normalizeGrade(profile.grade)) }
    var editingLanguage by remember(profile.language) { mutableStateOf(LanguageCatalog.normalize(profile.language)) }
    var draftAvatarBase64 by remember(profile.avatarBase64) { mutableStateOf(profile.avatarBase64) }
    var copiedNotice by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val compressed = uriToCompressedBase64(context, uri, maxDimension = 320)
            if (compressed != null) {
                draftAvatarBase64 = compressed
            }
        }
    }

    val level = LevelsCatalog.levelForCoins(profile.coins)
    val levelTitle = LevelsCatalog.displayTitleWithMilestone(profile.coins)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Profile Avatar, Grade, Language & Edit Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AvatarImage(
                        avatarBase64 = draftAvatarBase64,
                        studentName = editingName,
                        size = 96.dp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Level ${level.levelNumber}: $levelTitle",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${profile.country} • ${profile.phoneNumber} • $editingGrade",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("edit_profile_gallery_button")
                    ) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Pick Gallery Photo")
                    }

                    Spacer(Modifier.height(14.dp))
                    OutlinedTextField(
                        value = editingName,
                        onValueChange = { editingName = it },
                        label = { Text("Student Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_student_name_input")
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editingSchool,
                        onValueChange = { editingSchool = it },
                        label = { Text("School Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_school_name_input")
                    )

                    Spacer(Modifier.height(14.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = strings.selectGradeLabel,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            GradeCatalog.grades.forEach { gradeOpt ->
                                FilterChip(
                                    selected = editingGrade == gradeOpt,
                                    onClick = { editingGrade = gradeOpt },
                                    label = { Text(gradeOpt) },
                                    modifier = Modifier.testTag(
                                        "profile_grade_${gradeOpt.lowercase().replace(" ", "_")}"
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = strings.selectLanguageLabel,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "App navigation and Gemini Study Tutor replies will use your chosen language.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            LanguageCatalog.languages.forEach { langOpt ->
                                FilterChip(
                                    selected = editingLanguage == langOpt.englishName,
                                    onClick = { editingLanguage = langOpt.englishName },
                                    label = { Text(LanguageCatalog.displayLabel(langOpt.englishName)) },
                                    modifier = Modifier.testTag(
                                        "profile_language_${langOpt.code}"
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            onUpdateProfile(
                                editingName,
                                editingSchool,
                                editingGrade,
                                editingLanguage,
                                draftAvatarBase64
                            )
                        },
                        enabled = !isBusy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_profile_changes_button")
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isBusy) "Saving..." else strings.saveProfileChanges)
                    }
                }
            }
        }

        // Customer Care Card (8018026145)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.SupportAgent, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Customer Care Support", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Helpline Number: $CUSTOMER_CARE_NUMBER",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$CUSTOMER_CARE_NUMBER"))
                                context.startActivity(dialIntent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("call_customer_care_button")
                        ) {
                            Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Call Support")
                        }
                        OutlinedButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(CUSTOMER_CARE_NUMBER))
                                copiedNotice = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_customer_care_button")
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (copiedNotice) "Copied!" else "Copy")
                        }
                    }
                }
            }
        }

        // Sign Out
        item {
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("sign_out_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(strings.signOutLabel)
            }
        }
    }
}

@Composable
private fun LeaderboardTab(
    currentUid: String,
    leaderboardState: UiState<List<LeaderboardEntry>>
) {
    when (leaderboardState) {
        is UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        is UiState.Error -> Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
            Text(leaderboardState.message, color = MaterialTheme.colorScheme.error)
        }

        is UiState.Success -> {
            val ranked = leaderboardState.data.sortedByDescending { it.coins }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Top BrightQuiz Scholars",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                if (ranked.isEmpty()) {
                    item {
                        Text("No players ranked yet.", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    itemsIndexed(ranked, key = { idx, item -> item.userId.ifBlank { "$idx" } }) { index, entry ->
                        val rank = index + 1
                        val rankLabel = when (rank) {
                            1 -> "1st"
                            2 -> "2nd"
                            3 -> "3rd"
                            else -> "${rank}th"
                        }
                        val isMe = entry.userId == currentUid
                        val formattedCoins = NumberFormat.getNumberInstance(Locale.US).format(entry.coins)

                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    rank == 1 -> SunnyGoldLight
                                    isMe -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            ),
                            border = BorderStroke(
                                width = if (rank <= 3 || isMe) 1.5.dp else 1.dp,
                                color = if (rank == 1) SunnyGold else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (rank <= 3) SunnyGold else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.width(46.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = rankLabel,
                                            fontWeight = FontWeight.Bold,
                                            color = if (rank <= 3) OnSunnyGold else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                AvatarImage(
                                    avatarBase64 = entry.avatarBase64,
                                    studentName = entry.studentName,
                                    size = 44.dp
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isMe) "${entry.studentName} (You)" else entry.studentName,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "${entry.schoolName} • Lv ${entry.levelNumber} ${entry.levelTitle}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    if (rank == 1) {
                                        Icon(Icons.Filled.EmojiEvents, contentDescription = "1st place", tint = SunnyGold, modifier = Modifier.size(20.dp))
                                    }
                                    Text(
                                        text = "$formattedCoins c",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = if (rank == 1) OnSunnyGold else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendsTab(
    currentUid: String,
    leaderboardState: UiState<List<LeaderboardEntry>>,
    friendRequestsState: UiState<List<FriendRequest>>,
    onSendFriendRequest: (LeaderboardEntry) -> Unit,
    onRespondFriendRequest: (String, Boolean) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val requests = (friendRequestsState as? UiState.Success)?.data.orEmpty()
    val incomingPending = requests.filter { it.toUid == currentUid && it.status == "pending" }
    val acceptedFriends = requests.filter { it.status == "accepted" }
    val linkedUids = requests.flatMap { it.participants }.toSet()

    val players = (leaderboardState as? UiState.Success)?.data.orEmpty()
        .filter { it.userId != currentUid }
        .filter {
            searchQuery.isBlank() ||
                it.studentName.contains(searchQuery, ignoreCase = true) ||
                it.schoolName.contains(searchQuery, ignoreCase = true)
        }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (incomingPending.isNotEmpty()) {
            item {
                Text("Incoming Friend Requests", style = MaterialTheme.typography.titleLarge)
            }
            itemsIndexed(incomingPending, key = { _, req -> req.id }) { _, req ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(req.fromName, style = MaterialTheme.typography.titleMedium)
                            Text(req.fromSchool, style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(
                            onClick = { onRespondFriendRequest(req.id, true) },
                            modifier = Modifier.testTag("accept_friend_${req.id}")
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = "Accept friend request", tint = EmeraldCorrect)
                        }
                        IconButton(
                            onClick = { onRespondFriendRequest(req.id, false) },
                            modifier = Modifier.testTag("decline_friend_${req.id}")
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Decline friend request", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        item {
            Text("My Friends (${acceptedFriends.size})", style = MaterialTheme.typography.titleLarge)
        }
        if (acceptedFriends.isEmpty()) {
            item {
                Text(
                    text = "No friends added yet. Send a friend request below!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            itemsIndexed(acceptedFriends, key = { _, req -> "friend_${req.id}" }) { _, req ->
                val isFromMe = req.fromUid == currentUid
                val friendName = if (isFromMe) req.toName else req.fromName
                val friendSchool = if (isFromMe) req.toSchool else req.fromSchool
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = EmeraldContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AvatarImage(avatarBase64 = null, studentName = friendName, size = 40.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(friendName, style = MaterialTheme.typography.titleMedium, color = OnEmeraldContainer)
                            Text(friendSchool, style = MaterialTheme.typography.bodyMedium, color = OnEmeraldContainer)
                        }
                        Text("Friends", style = MaterialTheme.typography.labelLarge, color = EmeraldCorrect)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.GroupAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Add Friends", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search students by name or school") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("friend_search_input")
            )
        }

        if (players.isEmpty()) {
            item {
                Text(
                    text = "No matching students found yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            itemsIndexed(players, key = { _, p -> "find_${p.userId}" }) { _, player ->
                val existingReq = requests.firstOrNull { player.userId in it.participants }
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AvatarImage(
                            avatarBase64 = player.avatarBase64,
                            studentName = player.studentName,
                            size = 42.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(player.studentName, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "${player.schoolName} • Lv ${player.levelNumber}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (existingReq == null || player.userId !in linkedUids) {
                            Button(
                                onClick = { onSendFriendRequest(player) },
                                modifier = Modifier.testTag("add_friend_button_${player.userId}")
                            ) {
                                Icon(Icons.Filled.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Add")
                            }
                        } else {
                            TextButton(onClick = {}, enabled = false) {
                                Text(existingReq.status.replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                }
            }
        }
    }
}
