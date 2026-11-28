package com.example.data.repository

import android.content.Context
import com.example.data.model.QuestionCategory
import com.example.data.model.QuizQuestion
import kotlin.random.Random

/**
 * Persists Gemini Sign-In status & API key locally on the student's device so
 * once signed in on the Ask tab, the user is not prompted again on future visits.
 */
class GeminiSessionStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isSignedIn(): Boolean = !getApiKey().isNullOrBlank()

    fun getApiKey(): String? = prefs.getString(KEY_API_KEY, null)?.trim()?.takeIf { it.isNotEmpty() }

    fun getAccountLabel(): String = prefs.getString(KEY_ACCOUNT_LABEL, null).orEmpty().ifBlank { "Gemini Account" }

    fun saveSignIn(apiKey: String, accountLabel: String) {
        prefs.edit()
            .putString(KEY_API_KEY, apiKey.trim())
            .putString(KEY_ACCOUNT_LABEL, accountLabel.trim().ifEmpty { "Gemini Account" })
            .putBoolean(KEY_SIGNED_IN, true)
            .apply()
    }

    fun signOut() {
        prefs.edit()
            .remove(KEY_API_KEY)
            .remove(KEY_ACCOUNT_LABEL)
            .putBoolean(KEY_SIGNED_IN, false)
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "brightquiz_gemini_session"
        const val KEY_API_KEY = "gemini_api_key"
        const val KEY_ACCOUNT_LABEL = "gemini_account_label"
        const val KEY_SIGNED_IN = "gemini_signed_in"
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
