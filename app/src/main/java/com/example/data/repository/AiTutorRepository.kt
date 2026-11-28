package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.getLocalAppCheckDebugToken
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

class AiTutorRepository(
    private val appContext: Context? = null
) {

    private val primaryFlashModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = STUDY_SYSTEM_INSTRUCTION
        )
    }

    private val liteFallbackModel: GenerativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.5-flash-lite",
            systemInstruction = STUDY_SYSTEM_INSTRUCTION
        )
    }

    fun askStudyTutorStream(
        question: String,
        recentHistory: List<Pair<String, String>>,
        studentGrade: String,
        preferredLanguage: String
    ): Flow<String> = flow {
        val grade = studentGrade.ifBlank { "Grade 5" }
        val language = preferredLanguage.ifBlank { "English" }

        val historyContents = recentHistory.takeLast(6).map { (roleLabel, msg) ->
            val role = if (roleLabel.equals("Student", ignoreCase = true)) "user" else "model"
            content(role) { text(msg) }
        }

        val currentPrompt = buildString {
            append("[Student Grade: ")
            append(grade)
            append(" | Reply Language: ")
            append(language)
            append("]\n")
            append(question.trim())
        }

        val modelsToTry = listOf(primaryFlashModel, liteFallbackModel)
        var succeeded = false
        var lastException: Exception? = null

        for ((index, model) in modelsToTry.withIndex()) {
            val accumulated = StringBuilder()
            try {
                withTimeout(60_000L) {
                    val chat = model.startChat(history = historyContents)
                    chat.sendMessageStream(currentPrompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        if (chunkText.isNotEmpty()) {
                            accumulated.append(chunkText)
                            emit(accumulated.toString())
                        }
                    }
                }
                if (accumulated.isBlank()) {
                    throw IllegalStateException("Gemini returned an empty response. Please try asking again.")
                }
                succeeded = true
                break
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Firebase AI generation timed out on attempt $index", e)
                throw IllegalStateException("Gemini took too long to respond (60s timeout). Please try again.", e)
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI generation failed on attempt $index", e)
                lastException = e
                if (accumulated.isNotEmpty() || index == modelsToTry.lastIndex) {
                    throw enhanceErrorIfDebugAppCheck(e)
                }
            }
        }

        if (!succeeded && lastException != null) {
            throw enhanceErrorIfDebugAppCheck(lastException)
        }
    }

    private fun enhanceErrorIfDebugAppCheck(error: Exception): Exception {
        val raw = error.localizedMessage ?: error.message ?: "Unable to reach Gemini right now."
        val isAppCheckIssue = raw.contains("App Check", ignoreCase = true) ||
            raw.contains("403", ignoreCase = true) ||
            raw.contains("attestation", ignoreCase = true)

        if (BuildConfig.DEBUG && isAppCheckIssue && appContext != null) {
            val debugToken = getLocalAppCheckDebugToken(appContext)
            if (!debugToken.isNullOrBlank()) {
                return IllegalStateException(
                    "Debug APK App Check token is not registered for this phone yet. " +
                        "Register debug token \"$debugToken\" in Firebase Console -> App Check -> Manage debug tokens " +
                        "(Play Store release builds verify automatically without debug tokens).",
                    error
                )
            }
        }
        return IllegalStateException(raw, error)
    }

    private companion object {
        const val TAG = "AiTutorRepository"

        val STUDY_SYSTEM_INSTRUCTION = content {
            text(
                "You are Gemini Study Tutor in BrightQuiz, a kind academic tutor for school students. " +
                    "Follow the [Student Grade] and [Reply Language] provided in each student message. " +
                    "STRICT RULE: You ONLY answer questions about books, school studies, academic subjects, and tests/exam preparation. " +
                    "If the user's message is NOT about books, tests, or studies (such as video games, movies, gossip, casual greetings without study content, shopping, or non-academic chat), " +
                    "begin your reply with the exact marker [OFF_TOPIC] followed by a polite 1-2 sentence explanation in the requested Reply Language saying you only help with books, studies, and tests. " +
                    "If the question IS about books, studies, or tests, explain clearly for the stated Student Grade in the stated Reply Language without any [OFF_TOPIC] marker."
            )
        }
    }
}
