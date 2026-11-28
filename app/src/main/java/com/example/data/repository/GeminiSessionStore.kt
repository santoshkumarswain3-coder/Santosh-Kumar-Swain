package com.example.data.repository

import android.content.Context
import com.example.data.model.QuestionCategory
import com.example.data.model.QuizQuestion
import kotlin.random.Random

/**
 * Persists Google "Sign in to Gemini" status & account info locally on the device
 * so once a student signs in to Gemini on the Ask tab, they are not asked again.
 */
class GeminiSessionStore(context: Context, private val userId: String) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isSignedIn(): Boolean = prefs.getBoolean(signedInKey(), false)

    fun getAccountLabel(): String {
        val name = prefs.getString(nameKey(), null).orEmpty().trim()
        val email = prefs.getString(emailKey(), null).orEmpty().trim()
        return when {
            name.isNotEmpty() -> name
            email.isNotEmpty() -> email
            else -> "Gemini Connected"
        }
    }

    fun getAccountEmail(): String = prefs.getString(emailKey(), null).orEmpty()

    fun saveSignIn(email: String, displayName: String) {
        prefs.edit()
            .putBoolean(signedInKey(), true)
            .putString(emailKey(), email.trim())
            .putString(nameKey(), displayName.trim())
            .apply()
    }

    fun signOut() {
        prefs.edit()
            .putBoolean(signedInKey(), false)
            .remove(emailKey())
            .remove(nameKey())
            .apply()
    }

    private fun signedInKey() = "gemini_signed_in_$userId"
    private fun emailKey() = "gemini_email_$userId"
    private fun nameKey() = "gemini_name_$userId"

    private companion object {
        const val PREFS_NAME = "brightquiz_gemini_session"
    }
}

/**
 * Persists seen question IDs per user so consecutive tests serve fresh, non-repeating questions.
 */
class SeenQuestionsStore(context: Context, private val userId: String) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getSeenIds(): Set<String> =
        prefs.getStringSet(seenKey(), emptySet())?.toSet() ?: emptySet()

    fun getLastTestIds(): Set<String> =
        prefs.getStringSet(lastKey(), emptySet())?.toSet() ?: emptySet()

    fun recordServed(selectedIds: List<String>, fullPoolIds: Set<String>) {
        if (selectedIds.isEmpty()) return
        val selectedSet = selectedIds.toSet()
        val updatedSeen = getSeenIds() + selectedSet
        // If all questions in the pool have now been seen, start a fresh cycle keeping only the latest test excluded
        val finalSeen = if (fullPoolIds.isNotEmpty() && updatedSeen.containsAll(fullPoolIds)) {
            selectedSet
        } else {
            updatedSeen
        }
        prefs.edit()
            .putStringSet(seenKey(), finalSeen)
            .putStringSet(lastKey(), selectedSet)
            .apply()
    }

    private fun seenKey() = "seen_$userId"
    private fun lastKey() = "last_$userId"

    private companion object {
        const val PREFS_NAME = "brightquiz_seen_questions"
    }
}

object QuestionSelector {

    /**
     * Selects up to [requestedCount] distinct questions tailored to [userGrade]:
     * 1. Filters to [categoryFilter] when provided (falls back to [pool] only if that category is empty).
     * 2. Prioritizes unseen questions matching [userGrade] before seen ones.
     * 3. Excludes [lastTestIds] whenever possible so back-to-back tests never repeat the same set.
     * 4. Fills any remaining slots from closest adjacent grades (unseen first, then seen).
     */
    fun selectQuestions(
        pool: List<QuizQuestion>,
        userGrade: Int,
        requestedCount: Int,
        categoryFilter: QuestionCategory? = null,
        preferPicturesFirst: Boolean = false,
        seenIds: Set<String> = emptySet(),
        lastTestIds: Set<String> = emptySet(),
        random: Random = Random.Default
    ): List<QuizQuestion> {
        if (pool.isEmpty() || requestedCount <= 0) return emptyList()

        val scoped = if (categoryFilter != null) {
            val matchedCat = pool.filter { it.category == categoryFilter }
            if (matchedCat.isNotEmpty()) matchedCat else pool
        } else {
            pool
        }

        fun orderBucket(items: List<QuizQuestion>): List<QuizQuestion> {
            val shuffled = items.shuffled(random)
            return if (preferPicturesFirst) {
                shuffled.sortedByDescending { it.localImageRes != null || !it.imageBase64.isNullOrBlank() }
            } else {
                shuffled
            }
        }

        val gradeMatched = scoped.filter { it.matchesGrade(userGrade) }
        val otherGrades = scoped
            .filterNot { it.matchesGrade(userGrade) }
            .sortedBy { it.gradeDistance(userGrade) }

        val result = LinkedHashMap<String, QuizQuestion>(requestedCount)

        fun appendFrom(candidates: List<QuizQuestion>) {
            for (q in candidates) {
                if (result.size >= requestedCount) break
                result.putIfAbsent(q.id, q)
            }
        }

        // Priority 1: Unseen questions matching student's grade
        appendFrom(orderBucket(gradeMatched.filter { it.id !in seenIds }))
        // Priority 2: Unseen questions from closest adjacent grades (grouped by distance)
        if (result.size < requestedCount) {
            val unseenOther = otherGrades.filter { it.id !in seenIds }
            unseenOther.groupBy { it.gradeDistance(userGrade) }
                .toSortedMap()
                .values
                .forEach { group -> appendFrom(orderBucket(group)) }
        }
        // Priority 3: Seen grade-matched questions NOT in the immediately preceding test
        if (result.size < requestedCount) {
            appendFrom(orderBucket(gradeMatched.filter { it.id !in lastTestIds }))
        }
        // Priority 4: Seen adjacent-grade questions NOT in the immediately preceding test
        if (result.size < requestedCount) {
            appendFrom(orderBucket(otherGrades.filter { it.id !in lastTestIds }))
        }
        // Priority 5: Any remaining questions if total pool < requestedCount
        if (result.size < requestedCount) {
            appendFrom(orderBucket(gradeMatched + otherGrades))
        }

        return result.values.toList()
    }
}
