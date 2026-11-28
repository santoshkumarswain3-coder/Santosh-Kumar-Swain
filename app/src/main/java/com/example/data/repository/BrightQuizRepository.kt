package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.firebase.OperationType
import com.example.data.firebase.handleFirestoreError
import com.example.data.model.FriendRequest
import com.example.data.model.LeaderboardEntry
import com.example.data.model.LevelsCatalog
import com.example.data.model.QuestionCategory
import com.example.data.model.SharedQuestion
import com.example.data.model.TestAttempt
import com.example.data.model.TestType
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BrightQuizRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snap ->
                    if (snap.exists()) {
                        snap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    } else null
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    fun observeLeaderboard(): Flow<List<LeaderboardEntry>> = flow {
        val path = "leaderboard"
        emitAll(
            db.collection("leaderboard")
                .whereEqualTo("publicProfile", true)
                .limit(200)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(LeaderboardEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.coins }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser != null) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeTestHistory(userId: String): Flow<List<TestAttempt>> = flow {
        val path = "users/$userId/history"
        emitAll(
            db.collection("users").document(userId).collection("history")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(TestAttempt::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeSharedQuestions(): Flow<List<SharedQuestion>> = flow {
        val path = "questions"
        emitAll(
            db.collection("questions")
                .whereEqualTo("published", true)
                .limit(250)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(SharedQuestion::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser != null) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeFriendRequests(userId: String): Flow<List<FriendRequest>> = flow {
        val path = "friendRequests"
        emitAll(
            db.collection("friendRequests")
                .whereArrayContains("participants", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(FriendRequest::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createInitialProfile(
        country: String,
        phoneNumber: String,
        email: String,
        studentName: String,
        grade: String,
        schoolName: String,
        studyPasscode: String,
        avatarBase64: String?
    ): Result<Unit> {
        var path = "users"
        return try {
            val uid = requireUserId()
            val cleanCountry = country.trim().take(60)
            val cleanPhone = phoneNumber.trim().take(25)
            val cleanEmail = email.trim().take(120)
            val cleanName = studentName.trim().take(60)
            val cleanGrade = com.example.data.model.GradeCatalog.normalizeGrade(grade).take(30)
            val cleanSchool = schoolName.trim().take(100)
            require(cleanCountry.length >= 2) { "Please select your country." }
            require(cleanPhone.length >= 5) { "Please enter a valid phone number." }
            require(cleanEmail.length >= 3) { "Please enter a valid email address." }
            require(cleanName.isNotEmpty()) { "Please enter student name." }
            require(cleanSchool.isNotEmpty()) { "Please enter school name." }

            val dayKey = currentDayKey()
            val weekKey = currentWeekKey()
            val monthKey = currentMonthKey()
            val passcodeHash = if (studyPasscode.isNotBlank()) sha256Hash(studyPasscode) else null
            val safeAvatar = avatarBase64?.takeIf { it.length <= 250_000 }

            val userPayload = mapOf(
                "userId" to uid,
                "country" to cleanCountry,
                "phoneNumber" to cleanPhone,
                "email" to cleanEmail,
                "studentName" to cleanName,
                "grade" to cleanGrade,
                "language" to "English",
                "schoolName" to cleanSchool,
                "passcodeHash" to passcodeHash,
                "avatarBase64" to safeAvatar,
                "coins" to 0L,
                "dailyAskCount" to 0,
                "weeklyAskCount" to 0,
                "monthlyAskCount" to 0,
                "askDayKey" to dayKey,
                "askWeekKey" to weekKey,
                "askMonthKey" to monthKey,
                "dailyBonusDayKey" to "",
                "weeklyBonusWeekKey" to "",
                "monthlyBonusMonthKey" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null } as Map<String, Any>

            val level = LevelsCatalog.levelForCoins(0L)
            val leaderboardPayload = mapOf(
                "userId" to uid,
                "studentName" to cleanName,
                "schoolName" to cleanSchool,
                "coins" to 0L,
                "levelNumber" to level.levelNumber,
                "levelTitle" to LevelsCatalog.displayTitleWithMilestone(0L),
                "avatarBase64" to safeAvatar,
                "publicProfile" to true,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null } as Map<String, Any>

            val batch = db.batch()
            val userRef = db.collection("users").document(uid)
            val lbRef = db.collection("leaderboard").document(uid)
            path = userRef.path
            batch.set(userRef, userPayload)
            batch.set(lbRef, leaderboardPayload)
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateProfileDetails(
        studentName: String,
        schoolName: String,
        grade: String,
        language: String,
        avatarBase64: String?
    ): Result<Unit> {
        var path = "users"
        return try {
            val uid = requireUserId()
            val cleanName = studentName.trim().take(60)
            val cleanSchool = schoolName.trim().take(100)
            val cleanGrade = com.example.data.model.GradeCatalog.normalizeGrade(grade).take(30)
            val cleanLang = com.example.util.LanguageCatalog.normalize(language).take(40)
            require(cleanName.isNotEmpty()) { "Student name cannot be empty." }
            require(cleanSchool.isNotEmpty()) { "School name cannot be empty." }
            val safeAvatar = avatarBase64?.takeIf { it.length <= 250_000 }

            val userUpdates = mutableMapOf<String, Any>(
                "studentName" to cleanName,
                "schoolName" to cleanSchool,
                "grade" to cleanGrade,
                "language" to cleanLang,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            val lbUpdates = mutableMapOf<String, Any>(
                "studentName" to cleanName,
                "schoolName" to cleanSchool,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (safeAvatar != null) {
                userUpdates["avatarBase64"] = safeAvatar
                lbUpdates["avatarBase64"] = safeAvatar
            }

            val batch = db.batch()
            val userRef = db.collection("users").document(uid)
            val lbRef = db.collection("leaderboard").document(uid)
            path = userRef.path
            batch.update(userRef, userUpdates)
            batch.update(lbRef, lbUpdates)
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun recordTestCompletion(
        profile: UserProfile,
        testType: TestType,
        totalQuestions: Int,
        correctAnswers: Int,
        coinsEarned: Int,
        passed: Boolean,
        durationSeconds: Int
    ): Result<TestAttempt> {
        var path = "users/${profile.userId}/history"
        return try {
            val uid = requireUserId()
            check(uid == profile.userId) { "Authenticated user mismatch." }
            val attemptRef = db.collection("users").document(uid).collection("history").document()
            path = attemptRef.path

            val newCoins = (profile.coins + coinsEarned).coerceAtLeast(0L)
            val level = LevelsCatalog.levelForCoins(newCoins)
            val levelDisplay = LevelsCatalog.displayTitleWithMilestone(newCoins)

            val attemptMap = mapOf(
                "userId" to uid,
                "testType" to testType.name,
                "title" to testType.title,
                "totalQuestions" to totalQuestions.coerceIn(1, 100),
                "correctAnswers" to correctAnswers.coerceIn(0, totalQuestions),
                "coinsEarned" to coinsEarned.coerceIn(0, 2000),
                "passed" to passed,
                "studentName" to profile.studentName.take(60),
                "schoolName" to profile.schoolName.take(100),
                "durationSeconds" to durationSeconds.coerceIn(0, 3600),
                "createdAt" to FieldValue.serverTimestamp()
            )

            val batch = db.batch()
            batch.set(attemptRef, attemptMap)
            if (coinsEarned > 0) {
                batch.update(
                    db.collection("users").document(uid),
                    mapOf(
                        "coins" to newCoins,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                batch.update(
                    db.collection("leaderboard").document(uid),
                    mapOf(
                        "coins" to newCoins,
                        "levelNumber" to level.levelNumber,
                        "levelTitle" to levelDisplay,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
            }
            batch.commit().await()

            Result.success(
                TestAttempt(
                    id = attemptRef.id,
                    userId = uid,
                    testType = testType.name,
                    title = testType.title,
                    totalQuestions = totalQuestions,
                    correctAnswers = correctAnswers,
                    coinsEarned = coinsEarned,
                    passed = passed,
                    studentName = profile.studentName,
                    schoolName = profile.schoolName,
                    durationSeconds = durationSeconds
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun recordValidStudyQuestion(profile: UserProfile): Result<Int> {
        var path = "users/${profile.userId}"
        return try {
            val uid = requireUserId()
            val today = currentDayKey()
            val week = currentWeekKey()
            val month = currentMonthKey()

            val nextDaily = if (profile.askDayKey == today) profile.dailyAskCount + 1 else 1
            val nextWeekly = if (profile.askWeekKey == week) profile.weeklyAskCount + 1 else 1
            val nextMonthly = if (profile.askMonthKey == month) profile.monthlyAskCount + 1 else 1

            var bonusCoins = 0
            var nextDailyBonusKey = profile.dailyBonusDayKey
            var nextWeeklyBonusKey = profile.weeklyBonusWeekKey
            var nextMonthlyBonusKey = profile.monthlyBonusMonthKey

            if (nextDaily >= 5 && nextDailyBonusKey != today) {
                bonusCoins += 15
                nextDailyBonusKey = today
            }
            if (nextWeekly >= 25 && nextWeeklyBonusKey != week) {
                bonusCoins += 75
                nextWeeklyBonusKey = week
            }
            if (nextMonthly >= 50 && nextMonthlyBonusKey != month) {
                bonusCoins += 100
                nextMonthlyBonusKey = month
            }

            val updatedCoins = profile.coins + bonusCoins
            val level = LevelsCatalog.levelForCoins(updatedCoins)
            val levelDisplay = LevelsCatalog.displayTitleWithMilestone(updatedCoins)

            val batch = db.batch()
            val userRef = db.collection("users").document(uid)
            path = userRef.path
            batch.update(
                userRef,
                mapOf(
                    "dailyAskCount" to nextDaily,
                    "weeklyAskCount" to nextWeekly,
                    "monthlyAskCount" to nextMonthly,
                    "askDayKey" to today,
                    "askWeekKey" to week,
                    "askMonthKey" to month,
                    "dailyBonusDayKey" to nextDailyBonusKey,
                    "weeklyBonusWeekKey" to nextWeeklyBonusKey,
                    "monthlyBonusMonthKey" to nextMonthlyBonusKey,
                    "coins" to updatedCoins,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            if (bonusCoins > 0) {
                batch.update(
                    db.collection("leaderboard").document(uid),
                    mapOf(
                        "coins" to updatedCoins,
                        "levelNumber" to level.levelNumber,
                        "levelTitle" to levelDisplay,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
            }
            batch.commit().await()
            Result.success(bonusCoins)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun uploadQuestion(
        authorName: String,
        category: QuestionCategory,
        targetGrade: Int,
        questionText: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String,
        imageBase64: String?
    ): Result<Unit> {
        var path = "questions"
        return try {
            val uid = requireUserId()
            val cleanQuestion = questionText.trim().take(400)
            val cleanOptions = options.map { it.trim().take(150) }
            val cleanExplanation = explanation.trim().take(400)
            val safeTargetGrade = targetGrade.coerceIn(0, 12)
            require(cleanQuestion.length >= 5) { "Question must be at least 5 characters." }
            require(cleanOptions.size == 4 && cleanOptions.all { it.isNotEmpty() }) {
                "All 4 answer options are required."
            }
            require(correctIndex in 0..3) { "Please select the correct answer option." }

            val docRef = db.collection("questions").document()
            path = docRef.path
            val safeImage = imageBase64?.takeIf { it.length <= 250_000 }
            val payload = mapOf(
                "authorId" to uid,
                "authorName" to authorName.trim().ifEmpty { "Student" }.take(60),
                "category" to category.name,
                "targetGrade" to safeTargetGrade,
                "questionText" to cleanQuestion,
                "options" to cleanOptions,
                "correctIndex" to correctIndex,
                "explanation" to cleanExplanation,
                "imageBase64" to safeImage,
                "published" to true,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null } as Map<String, Any>

            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun sendFriendRequest(
        fromProfile: UserProfile,
        target: LeaderboardEntry
    ): Result<Unit> {
        var path = "friendRequests"
        return try {
            val uid = requireUserId()
            require(target.userId != uid) { "You cannot send a friend request to yourself." }
            val sortedIds = listOf(uid, target.userId).sorted()
            val reqId = "${sortedIds[0]}_${sortedIds[1]}"
            val docRef = db.collection("friendRequests").document(reqId)
            path = docRef.path

            val payload = mapOf(
                "fromUid" to uid,
                "fromName" to fromProfile.studentName.take(60),
                "fromSchool" to fromProfile.schoolName.take(100),
                "fromCoins" to fromProfile.coins,
                "toUid" to target.userId,
                "toName" to target.studentName.take(60),
                "toSchool" to target.schoolName.take(100),
                "toCoins" to target.coins,
                "participants" to listOf(uid, target.userId),
                "status" to "pending",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun respondFriendRequest(requestId: String, accept: Boolean): Result<Unit> {
        val path = "friendRequests/$requestId"
        return try {
            requireUserId()
            require(requestId.isNotBlank()) { "Invalid friend request." }
            val newStatus = if (accept) "accepted" else "declined"
            db.collection("friendRequests").document(requestId)
                .update(
                    mapOf(
                        "status" to newStatus,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    companion object {
        fun currentDayKey(): String =
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)

        fun currentWeekKey(): String {
            val cal = Calendar.getInstance()
            return "${cal.get(Calendar.YEAR)}-W${cal.get(Calendar.WEEK_OF_YEAR)}"
        }

        fun currentMonthKey(): String =
            SimpleDateFormat("yyyy-MM", Locale.US).format(Calendar.getInstance().time)
    }
}
