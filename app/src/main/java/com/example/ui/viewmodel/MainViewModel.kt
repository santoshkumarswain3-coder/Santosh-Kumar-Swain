package com.example.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.data.BuiltInQuestionBank
import com.example.data.model.ChatMessage
import com.example.data.model.FriendRequest
import com.example.data.model.LeaderboardEntry
import com.example.data.model.QuestionCategory
import com.example.data.model.QuizQuestion
import com.example.data.model.SharedQuestion
import com.example.data.model.TestAttempt
import com.example.data.model.TestType
import com.example.data.model.UiState
import com.example.data.model.UserProfile
import com.example.data.repository.AiTutorRepository
import com.example.data.repository.BrightQuizRepository
import com.example.data.repository.bitmapToCompressedBase64
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class ActiveTestSession(
    val testType: TestType,
    val questions: List<QuizQuestion>,
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val correctCount: Int = 0,
    val remainingSeconds: Int = 0,
    val elapsedSeconds: Int = 0,
    val finishedAttempt: TestAttempt? = null,
    val isSubmittingResult: Boolean = false
)

sealed interface AiImageState {
    data object Idle : AiImageState
    data object Loading : AiImageState
    data class Success(val bitmap: Bitmap, val base64: String) : AiImageState
    data class Error(val message: String) : AiImageState
}

data class AskTabState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "welcome",
            fromUser = false,
            text = "Hi! I am your BrightQuiz Study Tutor. Ask me anything about your tests, textbooks, science, history, geography, or GK! Ask 5 study questions/day (+15 coins), 25/week (+75 coins), or 50/month (+100 coins)."
        )
    ),
    val isStreaming: Boolean = false,
    val streamingText: String = "",
    val errorMessage: String? = null,
    val latestBonusMessage: String? = null
)

class MainViewModel(
    private val repository: BrightQuizRepository,
    private val aiRepository: AiTutorRepository,
    val currentUserId: String
) : ViewModel() {

    private fun <T> Flow<T>.toUiStateFlow(label: String): StateFlow<UiState<T>> =
        this.map<T, UiState<T>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Error observing $label", root)
                emit(UiState.Error(root.message ?: "Failed to load $label"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = UiState.Loading
            )

    val userProfileState: StateFlow<UiState<UserProfile?>> =
        repository.observeUserProfile(currentUserId).toUiStateFlow("student profile")

    val leaderboardState: StateFlow<UiState<List<LeaderboardEntry>>> =
        repository.observeLeaderboard().toUiStateFlow("leaderboard")

    val historyState: StateFlow<UiState<List<TestAttempt>>> =
        repository.observeTestHistory(currentUserId).toUiStateFlow("test history")

    val sharedQuestionsState: StateFlow<UiState<List<SharedQuestion>>> =
        repository.observeSharedQuestions().toUiStateFlow("uploaded questions")

    val friendRequestsState: StateFlow<UiState<List<FriendRequest>>> =
        repository.observeFriendRequests(currentUserId).toUiStateFlow("friend requests")

    val allQuestions: StateFlow<List<QuizQuestion>> = sharedQuestionsState
        .map { state ->
            val custom = (state as? UiState.Success)?.data.orEmpty().mapNotNull { it.toQuizQuestion() }
            custom + BuiltInQuestionBank.questions
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = BuiltInQuestionBank.questions
        )

    private val _activeTest = MutableStateFlow<ActiveTestSession?>(null)
    val activeTest: StateFlow<ActiveTestSession?> = _activeTest.asStateFlow()

    private val _askState = MutableStateFlow(AskTabState())
    val askState: StateFlow<AskTabState> = _askState.asStateFlow()

    private val _aiImageState = MutableStateFlow<AiImageState>(AiImageState.Idle)
    val aiImageState: StateFlow<AiImageState> = _aiImageState.asStateFlow()

    private val _actionFeedback = MutableStateFlow<String?>(null)
    val actionFeedback: StateFlow<String?> = _actionFeedback.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    private var timerJob: Job? = null

    fun clearActionFeedback() {
        _actionFeedback.value = null
    }

    fun resetAiImageState() {
        _aiImageState.value = AiImageState.Idle
    }

    fun clearBonusBanner() {
        _askState.value = _askState.value.copy(latestBonusMessage = null)
    }

    fun completeRegistration(
        country: String,
        phoneNumber: String,
        email: String,
        studentName: String,
        schoolName: String,
        studyPasscode: String,
        avatarBase64: String?
    ) {
        viewModelScope.launch {
            _isBusy.value = true
            val result = repository.createInitialProfile(
                country = country,
                phoneNumber = phoneNumber,
                email = email,
                studentName = studentName,
                schoolName = schoolName,
                studyPasscode = studyPasscode,
                avatarBase64 = avatarBase64
            )
            _isBusy.value = false
            result.onFailure { err ->
                _actionFeedback.value = err.localizedMessage ?: "Could not save registration."
            }
        }
    }

    fun updateProfile(studentName: String, schoolName: String, avatarBase64: String?) {
        viewModelScope.launch {
            _isBusy.value = true
            val result = repository.updateProfileDetails(studentName, schoolName, avatarBase64)
            _isBusy.value = false
            result.onSuccess {
                _actionFeedback.value = "Profile updated!"
            }.onFailure { err ->
                _actionFeedback.value = err.localizedMessage ?: "Failed to update profile."
            }
        }
    }

    fun generateAiAvatar(prompt: String) {
        viewModelScope.launch {
            _aiImageState.value = AiImageState.Loading
            val res = aiRepository.generateAvatarBitmap(prompt)
            res.onSuccess { bmp ->
                val b64 = bitmapToCompressedBase64(bmp, maxDimension = 320, quality = 76)
                _aiImageState.value = AiImageState.Success(bmp, b64)
            }.onFailure { err ->
                _aiImageState.value = AiImageState.Error(err.localizedMessage ?: "AI avatar generation failed.")
            }
        }
    }

    fun generateCurrentAffairsImage(prompt: String) {
        viewModelScope.launch {
            _aiImageState.value = AiImageState.Loading
            val res = aiRepository.generateCurrentAffairsBitmap(prompt)
            res.onSuccess { bmp ->
                val b64 = bitmapToCompressedBase64(bmp, maxDimension = 420, quality = 74)
                _aiImageState.value = AiImageState.Success(bmp, b64)
            }.onFailure { err ->
                _aiImageState.value = AiImageState.Error(err.localizedMessage ?: "Illustration generation failed.")
            }
        }
    }

    fun uploadCustomQuestion(
        authorName: String,
        category: QuestionCategory,
        questionText: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String,
        imageBase64: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isBusy.value = true
            val res = repository.uploadQuestion(
                authorName = authorName,
                category = category,
                questionText = questionText,
                options = options,
                correctIndex = correctIndex,
                explanation = explanation,
                imageBase64 = imageBase64
            )
            _isBusy.value = false
            res.onSuccess {
                _actionFeedback.value = "MCQ added to the shared bank!"
                onSuccess()
            }.onFailure { err ->
                _actionFeedback.value = err.localizedMessage ?: "Could not upload question."
            }
        }
    }

    fun startTest(testType: TestType, categoryFilter: QuestionCategory? = null) {
        val pool = allQuestions.value
        val filtered = if (categoryFilter != null) {
            val catList = pool.filter { it.category == categoryFilter }
            if (catList.isNotEmpty()) catList else pool
        } else if (testType == TestType.PRACTICE) {
            // Put picture questions and uploaded questions first for engaging practice
            pool.sortedByDescending { it.localImageRes != null || !it.imageBase64.isNullOrBlank() }
        } else {
            pool.shuffled()
        }

        val selected = filtered.take(testType.questionCount.coerceAtMost(filtered.size))
        if (selected.isEmpty()) {
            _actionFeedback.value = "No questions available yet."
            return
        }

        timerJob?.cancel()
        _activeTest.value = ActiveTestSession(
            testType = testType,
            questions = selected,
            currentIndex = 0,
            selectedOptionIndex = null,
            correctCount = 0,
            remainingSeconds = testType.durationSeconds,
            elapsedSeconds = 0
        )

        if (testType.durationSeconds > 0) {
            timerJob = viewModelScope.launch {
                while (true) {
                    delay(1000L)
                    val cur = _activeTest.value ?: break
                    if (cur.finishedAttempt != null || cur.isSubmittingResult) break
                    val nextRem = (cur.remainingSeconds - 1).coerceAtLeast(0)
                    val nextElapsed = cur.elapsedSeconds + 1
                    _activeTest.value = cur.copy(
                        remainingSeconds = nextRem,
                        elapsedSeconds = nextElapsed
                    )
                    if (nextRem == 0) {
                        finishActiveTest()
                        break
                    }
                }
            }
        } else {
            timerJob = viewModelScope.launch {
                while (true) {
                    delay(1000L)
                    val cur = _activeTest.value ?: break
                    if (cur.finishedAttempt != null || cur.isSubmittingResult) break
                    _activeTest.value = cur.copy(elapsedSeconds = cur.elapsedSeconds + 1)
                }
            }
        }
    }

    fun selectAnswerOption(optionIndex: Int) {
        val cur = _activeTest.value ?: return
        if (cur.selectedOptionIndex != null || cur.finishedAttempt != null) return
        val question = cur.questions.getOrNull(cur.currentIndex) ?: return
        val isCorrect = optionIndex == question.correctIndex
        _activeTest.value = cur.copy(
            selectedOptionIndex = optionIndex,
            correctCount = if (isCorrect) cur.correctCount + 1 else cur.correctCount
        )
    }

    fun advanceOrFinishTest() {
        val cur = _activeTest.value ?: return
        if (cur.finishedAttempt != null || cur.isSubmittingResult) return
        if (cur.currentIndex + 1 < cur.questions.size) {
            _activeTest.value = cur.copy(
                currentIndex = cur.currentIndex + 1,
                selectedOptionIndex = null
            )
        } else {
            finishActiveTest()
        }
    }

    fun finishActiveTest() {
        val cur = _activeTest.value ?: return
        if (cur.finishedAttempt != null || cur.isSubmittingResult) return
        timerJob?.cancel()
        val profile = (userProfileState.value as? UiState.Success)?.data
        val total = cur.questions.size.coerceAtLeast(1)
        val correct = cur.correctCount
        val passed = correct * 100 >= total * 60
        val coinsEarned = if (passed) cur.testType.coinReward else 0

        if (profile == null) {
            _activeTest.value = cur.copy(
                finishedAttempt = TestAttempt(
                    userId = currentUserId,
                    testType = cur.testType.name,
                    title = cur.testType.title,
                    totalQuestions = total,
                    correctAnswers = correct,
                    coinsEarned = 0,
                    passed = passed,
                    studentName = "Student",
                    schoolName = "BrightQuiz",
                    durationSeconds = cur.elapsedSeconds
                )
            )
            return
        }

        _activeTest.value = cur.copy(isSubmittingResult = true)
        viewModelScope.launch {
            val res = repository.recordTestCompletion(
                profile = profile,
                testType = cur.testType,
                totalQuestions = total,
                correctAnswers = correct,
                coinsEarned = coinsEarned,
                passed = passed,
                durationSeconds = cur.elapsedSeconds
            )
            val latest = _activeTest.value ?: return@launch
            res.onSuccess { attempt ->
                _activeTest.value = latest.copy(
                    isSubmittingResult = false,
                    finishedAttempt = attempt
                )
            }.onFailure { err ->
                _actionFeedback.value = err.localizedMessage ?: "Failed to save test score."
                _activeTest.value = latest.copy(
                    isSubmittingResult = false,
                    finishedAttempt = TestAttempt(
                        userId = currentUserId,
                        testType = cur.testType.name,
                        title = cur.testType.title,
                        totalQuestions = total,
                        correctAnswers = correct,
                        coinsEarned = 0,
                        passed = passed,
                        studentName = profile.studentName,
                        schoolName = profile.schoolName,
                        durationSeconds = cur.elapsedSeconds
                    )
                )
            }
        }
    }

    fun closeActiveTest() {
        timerJob?.cancel()
        _activeTest.value = null
    }

    fun askStudyQuestion(questionText: String) {
        val trimmed = questionText.trim()
        if (trimmed.isEmpty() || _askState.value.isStreaming) return
        val profile = (userProfileState.value as? UiState.Success)?.data

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            fromUser = true,
            text = trimmed
        )
        val historyPairs = _askState.value.messages
            .filter { !it.offTopic }
            .takeLast(6)
            .map { (if (it.fromUser) "Student" else "Tutor") to it.text }

        _askState.value = _askState.value.copy(
            messages = _askState.value.messages + userMsg,
            isStreaming = true,
            streamingText = "",
            errorMessage = null,
            latestBonusMessage = null
        )

        viewModelScope.launch {
            var finalReply = ""
            try {
                aiRepository.askStudyTutorStream(trimmed, historyPairs).collect { partial ->
                    finalReply = partial
                    val cleanPreview = partial.removePrefix("[OFF_TOPIC]").trimStart()
                    _askState.value = _askState.value.copy(streamingText = cleanPreview)
                }

                val isOffTopic = finalReply.trimStart().startsWith("[OFF_TOPIC]")
                val cleanReply = finalReply.trimStart().removePrefix("[OFF_TOPIC]").trim()
                val aiMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    fromUser = false,
                    text = cleanReply.ifEmpty { "Please ask a question about your tests, books, or studies." },
                    offTopic = isOffTopic
                )

                var bonusMsg: String? = null
                if (!isOffTopic && profile != null) {
                    val bonusRes = repository.recordValidStudyQuestion(profile)
                    bonusRes.onSuccess { awarded ->
                        if (awarded > 0) {
                            bonusMsg = "Study Streak Bonus! You earned +$awarded coins!"
                        }
                    }
                }

                _askState.value = _askState.value.copy(
                    messages = _askState.value.messages + aiMsg,
                    isStreaming = false,
                    streamingText = "",
                    latestBonusMessage = bonusMsg
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Study Tutor error", e)
                _askState.value = _askState.value.copy(
                    isStreaming = false,
                    streamingText = "",
                    errorMessage = e.localizedMessage ?: "Unable to reach Study Tutor right now."
                )
            }
        }
    }

    fun sendFriendRequest(target: LeaderboardEntry) {
        val profile = (userProfileState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            val res = repository.sendFriendRequest(profile, target)
            res.onSuccess {
                _actionFeedback.value = "Friend request sent to ${target.studentName}!"
            }.onFailure { err ->
                _actionFeedback.value = err.localizedMessage ?: "Failed to send friend request."
            }
        }
    }

    fun respondToFriendRequest(requestId: String, accept: Boolean) {
        viewModelScope.launch {
            val res = repository.respondFriendRequest(requestId, accept)
            res.onSuccess {
                _actionFeedback.value = if (accept) "Friend request accepted!" else "Friend request declined."
            }.onFailure { err ->
                _actionFeedback.value = err.localizedMessage ?: "Failed to update request."
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    companion object {
        private const val TAG = "MainViewModel"

        fun provideFactory(currentUserId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val db = FirebaseFirestore.getInstance(app.getString(R.string.firestore_database_id))
                MainViewModel(
                    repository = BrightQuizRepository(db),
                    aiRepository = AiTutorRepository(),
                    currentUserId = currentUserId
                )
            }
        }
    }
}
