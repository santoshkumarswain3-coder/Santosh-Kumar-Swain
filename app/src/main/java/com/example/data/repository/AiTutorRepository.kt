package com.example.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

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

        val accumulated = StringBuilder()
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
    }

    suspend fun generateAvatarBitmap(characterPrompt: String): Result<Bitmap> {
        return try {
            val sanitized = characterPrompt.trim().ifBlank { "cheerful young scholar with glasses and stars" }
            val fullPrompt = "Friendly colorful Pixar-style 3D portrait avatar for a student profile badge: $sanitized, bright warm studio lighting, clean pastel background, centered head-and-shoulders portrait, no text"
            val bitmap = withTimeout(60_000L) {
                val response = imageModel.generateContent(fullPrompt)
                val imagePart = response.candidates.firstOrNull()
                    ?.content?.parts?.filterIsInstance<ImagePart>()
                    ?.firstOrNull()
                imagePart?.image
            }
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
            val bitmap = withTimeout(60_000L) {
                val response = imageModel.generateContent(fullPrompt)
                val imagePart = response.candidates.firstOrNull()
                    ?.content?.parts?.filterIsInstance<ImagePart>()
                    ?.firstOrNull()
                imagePart?.image
            }
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

    private companion object {
        const val TAG = "AiTutorRepo"
    }
}
