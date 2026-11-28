package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class TestType(
    val title: String,
    val questionCount: Int,
    val durationSeconds: Int,
    val coinReward: Int,
    val subtitle: String
) {
    DAILY(
        title = "Daily GK Test",
        questionCount = 10,
        durationSeconds = 60,
        coinReward = 10,
        subtitle = "10 MCQs • 1 Min Timer • +10 Coins"
    ),
    WEEKLY(
        title = "Weekly Challenge",
        questionCount = 25,
        durationSeconds = 300,
        coinReward = 100,
        subtitle = "25 MCQs • 5 Min Timer • +100 Coins"
    ),
    MONTHLY(
        title = "Monthly Grand Quiz",
        questionCount = 50,
        durationSeconds = 600,
        coinReward = 1000,
        subtitle = "50 MCQs • 10 Min Timer • +1,000 Coins"
    ),
    PRACTICE(
        title = "Picture & GK Practice",
        questionCount = 15,
        durationSeconds = 0,
        coinReward = 0,
        subtitle = "Untimed • Subject & Picture MCQs"
    )
}

enum class QuestionCategory(val displayName: String) {
    CURRENT_AFFAIRS("Current Affairs"),
    SCIENCE_SPACE("Science & Space"),
    MATHEMATICS("Mathematics"),
    ENGLISH_LITERATURE("English & Books"),
    GEOGRAPHY("Geography"),
    HISTORY_CIVICS("History & Civics"),
    COMPUTER_TECH("Computer & Tech"),
    ENVIRONMENT_NATURE("Environment & Nature"),
    SPORTS_ARTS("Sports & Arts"),
    LOGICAL_REASONING("Logical Reasoning"),
    GENERAL("General Knowledge");

    companion object {
        fun fromRaw(raw: String): QuestionCategory =
            entries.firstOrNull { it.name == raw } ?: GENERAL
    }
}

object GradeCatalog {
    val grades: List<String> = (1..12).map { "Grade $it" }

    fun parseGradeNumber(raw: String?): Int {
        if (raw.isNullOrBlank()) return 5
        val digits = raw.filter { it.isDigit() }.toIntOrNull()
        return (digits ?: 5).coerceIn(1, 12)
    }

    fun normalizeGrade(raw: String?): String = "Grade ${parseGradeNumber(raw)}"
}

data class LevelTier(
    val levelNumber: Int,
    val title: String,
    val minCoins: Long,
    val badgeNote: String
)

object LevelsCatalog {
    val tiers = listOf(
        LevelTier(0, "Starter", 0L, "Earn 10 coins to reach Level 1!"),
        LevelTier(1, "Beginner", 10L, "10 Coins • Great start!"),
        LevelTier(2, "Good", 100L, "100 Coins • Keep growing!"),
        LevelTier(3, "Very Good", 2_000L, "2,000 Coins • Rising star!"),
        LevelTier(4, "Excellent", 10_000L, "10k Coins • Sharp scholar!"),
        LevelTier(5, "WoW", 20_000L, "20k Coins • 40k unlocks WOW Very nice!"),
        LevelTier(6, "Go ahead", 80_000L, "80k Coins • Unstoppable momentum!"),
        LevelTier(7, "Almost Expert", 100_000L, "1 Lakh Coins • Elite thinker!"),
        LevelTier(8, "Expert", 200_000L, "2 Lakh Coins • Quiz champion!"),
        LevelTier(9, "EXCLUSIVE", 500_000L, "5 Lakh Coins • Exclusive hall of fame!"),
        LevelTier(10, "Being Master", 10_000_000L, "10M Coins • Unlocks Being Master Certificate!")
    )

    const val WOW_VERY_NICE_COINS = 40_000L
    const val MASTER_COINS = 10_000_000L

    fun levelForCoins(coins: Long): LevelTier =
        tiers.lastOrNull { coins >= it.minCoins } ?: tiers.first()

    fun displayTitleWithMilestone(coins: Long): String {
        val base = levelForCoins(coins)
        return if (base.levelNumber == 5 && coins >= WOW_VERY_NICE_COINS) {
            "WOW Very nice"
        } else {
            base.title
        }
    }

    fun nextLevel(coins: Long): LevelTier? {
        val current = levelForCoins(coins)
        return tiers.firstOrNull { it.levelNumber == current.levelNumber + 1 }
    }
}

data class QuizQuestion(
    val id: String,
    val category: QuestionCategory,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val minGrade: Int = 1,
    val maxGrade: Int = 12,
    @DrawableRes val localImageRes: Int? = null,
    val imageBase64: String? = null,
    val authorName: String = "BrightQuiz Editorial"
) {
    fun matchesGrade(gradeNumber: Int): Boolean = gradeNumber in minGrade..maxGrade

    fun gradeDistance(gradeNumber: Int): Int = when {
        gradeNumber < minGrade -> minGrade - gradeNumber
        gradeNumber > maxGrade -> gradeNumber - maxGrade
        else -> 0
    }
}

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val country: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val studentName: String = "",
    val grade: String = "Grade 5",
    val language: String = "English",
    val schoolName: String = "",
    val passcodeHash: String? = null,
    val avatarBase64: String? = null,
    val coins: Long = 0L,
    val dailyAskCount: Int = 0,
    val weeklyAskCount: Int = 0,
    val monthlyAskCount: Int = 0,
    val askDayKey: String = "",
    val askWeekKey: String = "",
    val askMonthKey: String = "",
    val dailyBonusDayKey: String = "",
    val weeklyBonusWeekKey: String = "",
    val monthlyBonusMonthKey: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class LeaderboardEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val studentName: String = "",
    val schoolName: String = "",
    val coins: Long = 0L,
    val levelNumber: Int = 0,
    val levelTitle: String = "Starter",
    val avatarBase64: String? = null,
    val publicProfile: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class TestAttempt(
    @DocumentId val id: String = "",
    val userId: String = "",
    val testType: String = TestType.DAILY.name,
    val title: String = "",
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val coinsEarned: Int = 0,
    val passed: Boolean = false,
    val studentName: String = "",
    val schoolName: String = "",
    val durationSeconds: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SharedQuestion(
    @DocumentId val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val category: String = QuestionCategory.GENERAL.name,
    val targetGrade: Int = 0,
    val questionText: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val explanation: String = "",
    val imageBase64: String? = null,
    val published: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toQuizQuestion(): QuizQuestion? {
        if (questionText.isBlank() || options.size != 4 || correctIndex !in 0..3) return null
        val minG = if (targetGrade in 1..12) (targetGrade - 1).coerceAtLeast(1) else 1
        val maxG = if (targetGrade in 1..12) (targetGrade + 1).coerceAtMost(12) else 12
        return QuizQuestion(
            id = id.ifBlank { questionText.hashCode().toString() },
            category = QuestionCategory.fromRaw(category),
            questionText = questionText,
            options = options,
            correctIndex = correctIndex,
            explanation = explanation,
            minGrade = minG,
            maxGrade = maxG,
            localImageRes = null,
            imageBase64 = imageBase64,
            authorName = authorName.ifBlank { "Community" }
        )
    }
}

@Keep
@IgnoreExtraProperties
data class FriendRequest(
    @DocumentId val id: String = "",
    val fromUid: String = "",
    val fromName: String = "",
    val fromSchool: String = "",
    val fromCoins: Long = 0L,
    val toUid: String = "",
    val toName: String = "",
    val toSchool: String = "",
    val toCoins: Long = 0L,
    val participants: List<String> = emptyList(),
    val status: String = "pending",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class ChatMessage(
    val id: String,
    val fromUser: Boolean,
    val text: String,
    val offTopic: Boolean = false
)

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
