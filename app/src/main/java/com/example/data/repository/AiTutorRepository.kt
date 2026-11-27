package com.example.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.data.firebase.AppCheckInitState
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.appcheck.AppCheckToken
import com.google.firebase.appcheck.FirebaseAppCheck
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AiTutorRepository {

    private val studyTutorModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "You are BrightQuiz Study Tutor, a kind, encouraging academic tutor for school children. " +
                        "STRICT RULE: You ONLY answer questions about school studies, books, literature, science, math, history, geography, general knowledge, current affairs for students, and test/exam preparation. " +
                        "If the user's message is NOT about books, tests, or studies (such as video games, movies, gossip, personal chat, shopping, or non-academic entertainment), " +
                        "begin your reply with the exact marker [OFF_TOPIC] followed by a friendly 1-2 sentence explanation that you only answer questions about tests, books, and studies. " +
                        "If the question IS about books, studies, or tests, explain clearly in simple, child-friendly language without any [OFF_TOPIC] marker."
                )
            }
        )
    }

    private val imageModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-nano-banana-2.1",
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
            }
        )
    }

    private suspend fun ensureValidAppCheckToken(forceRefresh: Boolean = false) {
        try {
            val token = withTimeout(20_000L) {
                suspendCancellableCoroutine<AppCheckToken> { continuation ->
                    FirebaseAppCheck.getInstance()
                        .getAppCheckToken(forceRefresh)
                        .addOnSuccessListener { result ->
                            if (continuation.isActive) continuation.resume(result)
                        }
                        .addOnFailureListener { error ->
                            if (continuation.isActive) continuation.resumeWithException(error)
                        }
                }
            }
            if (token.token.isBlank()) {
                throw IllegalStateException(
                    "App Check (${AppCheckInitState.providerSummary}) returned an empty token."
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                TAG,
                "App Check token retrieval failed (provider=${AppCheckInitState.providerSummary}, forceRefresh=$forceRefresh)",
                e
            )
            throw IllegalStateException(
                "App Check (${AppCheckInitState.providerSummary}) failed: ${e.localizedMessage ?: e.javaClass.simpleName}",
                e
            )
        }
    }

    private fun isAppCheckServerError(error: Throwable): Boolean {
        val msg = error.message.orEmpty()
        return msg.contains("App Check", ignoreCase = true) ||
            msg.contains("appcheck", ignoreCase = true)
    }

    fun askStudyTutorStream(question: String, recentHistory: List<Pair<String, String>>): Flow<String> = flow {
        val contextPrompt = buildString {
            if (recentHistory.isNotEmpty()) {
                appendLine("Recent study conversation:")
                recentHistory.takeLast(4).forEach { (role, msg) ->
                    appendLine("$role: $msg")
                }
                appendLine()
            }
            append("Student question: ")
            append(question.trim())
        }

        var attempt = 0
        while (true) {
            ensureValidAppCheckToken(forceRefresh = attempt > 0)
            val accumulated = StringBuilder()
            try {
                withTimeout(60_000L) {
                    studyTutorModel.generateContentStream(contextPrompt).collect { chunk ->
                        val text = chunk.text.orEmpty()
                        if (text.isNotEmpty()) {
                            accumulated.append(text)
                            emit(accumulated.toString())
                        }
                    }
                }
                if (accumulated.isBlank()) {
                    throw IllegalStateException("Study tutor returned an empty response.")
                }
                break
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (attempt == 0 && accumulated.isEmpty() && isAppCheckServerError(e)) {
                    attempt++
                    Log.w(
                        TAG,
                        "Retrying Study Tutor after force-refreshing App Check token (${AppCheckInitState.providerSummary})",
                        e
                    )
                    continue
                }
                if (isAppCheckServerError(e)) {
                    throw IllegalStateException(
                        "${e.localizedMessage ?: "Firebase App Check token is invalid."} [${AppCheckInitState.providerSummary}]",
                        e
                    )
                }
                throw e
            }
        }
    }

    suspend fun generateAvatarBitmap(characterPrompt: String): Result<Bitmap> {
        return try {
            val sanitized = characterPrompt.trim().ifBlank { "cheerful young scholar with glasses and stars" }
            val fullPrompt = "Friendly colorful Pixar-style 3D portrait avatar for a student profile badge: $sanitized, bright warm studio lighting, clean pastel background, centered head-and-shoulders portrait, no text"
            val bitmap = generateImageWithAppCheckRetry(fullPrompt)
            if (bitmap != null) {
                Result.success(bitmap)
            } else {
                Result.failure(IllegalStateException("No image was returned by Gemini."))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI avatar generation timed out", e)
            Result.failure(Exception("Avatar generation timed out after 60 seconds. Please try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI avatar generation failed", e)
            Result.failure(e)
        }
    }

    suspend fun generateCurrentAffairsBitmap(topicPrompt: String): Result<Bitmap> {
        return try {
            val sanitized = topicPrompt.trim().ifBlank { "world globe with science discovery icons" }
            val fullPrompt = "Child-friendly colorful editorial vector illustration for a school current affairs quiz card: $sanitized, crisp clean details, bright lighting, no text or labels"
            val bitmap = generateImageWithAppCheckRetry(fullPrompt)
            if (bitmap != null) {
                Result.success(bitmap)
            } else {
                Result.failure(IllegalStateException("No illustration was returned by Gemini."))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Current affairs illustration timed out", e)
            Result.failure(Exception("Illustration timed out after 60 seconds. Please try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Current affairs illustration failed", e)
            Result.failure(e)
        }
    }

    private suspend fun generateImageWithAppCheckRetry(fullPrompt: String): Bitmap? {
        var attempt = 0
        while (true) {
            ensureValidAppCheckToken(forceRefresh = attempt > 0)
            try {
                return withTimeout(60_000L) {
                    val response = imageModel.generateContent(fullPrompt)
                    val imagePart = response.candidates.firstOrNull()
                        ?.content?.parts?.filterIsInstance<ImagePart>()
                        ?.firstOrNull()
                    imagePart?.image
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (attempt == 0 && isAppCheckServerError(e)) {
                    attempt++
                    Log.w(
                        TAG,
                        "Retrying image generation after force-refreshing App Check token (${AppCheckInitState.providerSummary})",
                        e
                    )
                    continue
                }
                if (isAppCheckServerError(e)) {
                    throw IllegalStateException(
                        "${e.localizedMessage ?: "Firebase App Check token is invalid."} [${AppCheckInitState.providerSummary}]",
                        e
                    )
                }
                throw e
            }
        }
    }

    private companion object {
        const val TAG = "AiTutorRepo"
    }
}
