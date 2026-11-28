package com.example.data.repository

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GeminiAuthException(message: String) : IllegalStateException(message)

class AiTutorRepository {

    suspend fun verifyGeminiKey(apiKey: String): Result<Unit> = withContext(Dispatchers.IO) {
        val cleanKey = apiKey.trim()
        if (cleanKey.length < 20) {
            return@withContext Result.failure(
                IllegalArgumentException("Please enter a valid Gemini API key from Google AI Studio.")
            )
        }
        var lastError: Exception? = null
        for (model in MODEL_CANDIDATES) {
            try {
                val url = URL("$BASE_URL/$model:generateContent")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 15_000
                    readTimeout = 20_000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("x-goog-api-key", cleanKey)
                }
                val payload = JSONObject().apply {
                    put(
                        "contents",
                        JSONArray().put(
                            JSONObject().apply {
                                put("role", "user")
                                put("parts", JSONArray().put(JSONObject().put("text", "Reply with OK.")))
                            }
                        )
                    )
                    put("generationConfig", JSONObject().put("maxOutputTokens", 8))
                }
                OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(payload.toString())
                }
                val code = conn.responseCode
                if (code in 200..299) {
                    conn.disconnect()
                    return@withContext Result.success(Unit)
                }
                val errorText = conn.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                conn.disconnect()
                if (code == 404) {
                    lastError = IllegalStateException("Model $model unavailable ($code)")
                    continue
                }
                val isKeyError = code == 401 || code == 403 ||
                    (code == 400 && errorText.contains("key", ignoreCase = true))
                if (isKeyError) {
                    val friendly = parseApiErrorMessage(errorText).ifBlank {
                        "Invalid Gemini API key (HTTP $code). Please check your key in Google AI Studio."
                    }
                    return@withContext Result.failure(GeminiAuthException(friendly))
                }
                lastError = IllegalStateException("Gemini check failed (HTTP $code): ${parseApiErrorMessage(errorText)}")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                lastError = e
            }
        }
        Result.failure(lastError ?: IllegalStateException("Unable to verify Gemini key right now."))
    }

    fun askStudyTutorStream(
        apiKey: String,
        question: String,
        recentHistory: List<Pair<String, String>>,
        studentGrade: String,
        preferredLanguage: String
    ): Flow<String> = flow {
        val cleanKey = apiKey.trim()
        if (cleanKey.isBlank()) {
            throw GeminiAuthException("Please sign in to Gemini before asking questions.")
        }

        val systemText = buildString {
            append("You are Gemini Study Tutor in BrightQuiz, a kind academic tutor for a student in ")
            append(studentGrade.ifBlank { "Grade 5" })
            append(". ")
            append("STRICT RULE: You ONLY answer questions about books, school studies, academic subjects, and tests/exam preparation. ")
            append("If the user's message is NOT about books, tests, or studies (such as video games, movies, gossip, casual greetings without study content, shopping, or non-academic chat), ")
            append("begin your reply with the exact marker [OFF_TOPIC] followed by a polite 1-2 sentence explanation in ")
            append(preferredLanguage.ifBlank { "English" })
            append(" saying you only help with books, studies, and tests. ")
            append("If the question IS about books, studies, or tests, explain clearly for a ")
            append(studentGrade.ifBlank { "Grade 5" })
            append(" student in ")
            append(preferredLanguage.ifBlank { "English" })
            append(" without any [OFF_TOPIC] marker.")
        }

        val contentsArray = JSONArray()
        recentHistory.takeLast(6).forEach { (roleLabel, msg) ->
            val role = if (roleLabel.equals("Student", ignoreCase = true)) "user" else "model"
            contentsArray.put(
                JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().put(JSONObject().put("text", msg)))
                }
            )
        }
        contentsArray.put(
            JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", question.trim())))
            }
        )

        val requestJson = JSONObject().apply {
            put(
                "systemInstruction",
                JSONObject().put(
                    "parts",
                    JSONArray().put(JSONObject().put("text", systemText))
                )
            )
            put("contents", contentsArray)
        }

        var succeeded = false
        var lastException: Exception? = null

        for (model in MODEL_CANDIDATES) {
            val accumulated = StringBuilder()
            try {
                val url = URL("$BASE_URL/$model:streamGenerateContent?alt=sse")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 20_000
                    readTimeout = 60_000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("x-goog-api-key", cleanKey)
                }
                OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(requestJson.toString())
                }

                val code = conn.responseCode
                if (code !in 200..299) {
                    val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                    conn.disconnect()
                    val apiMsg = parseApiErrorMessage(errBody)
                    if (code == 404) {
                        lastException = IllegalStateException("Model $model not available: $apiMsg")
                        continue
                    }
                    val isKeyError = code == 401 || code == 403 ||
                        (code == 400 && errBody.contains("key", ignoreCase = true))
                    if (isKeyError) {
                        throw GeminiAuthException(
                            apiMsg.ifBlank { "Gemini sign-in failed (HTTP $code). Please sign in to Gemini again." }
                        )
                    }
                    throw IllegalStateException(
                        apiMsg.ifBlank { "Gemini request failed (HTTP $code)." }
                    )
                }

                BufferedReader(InputStreamReader(conn.inputStream, Charsets.UTF_8)).use { reader ->
                    while (true) {
                        val line = reader.readLine() ?: break
                        if (!line.startsWith("data:")) continue
                        val dataPayload = line.removePrefix("data:").trim()
                        if (dataPayload.isEmpty() || dataPayload == "[DONE]") continue
                        val chunkText = extractCandidateText(dataPayload)
                        if (chunkText.isNotEmpty()) {
                            accumulated.append(chunkText)
                            emit(accumulated.toString())
                        }
                    }
                }
                conn.disconnect()
                if (accumulated.isBlank()) {
                    throw IllegalStateException("Gemini returned an empty response. Please try asking again.")
                }
                succeeded = true
                break
            } catch (e: CancellationException) {
                throw e
            } catch (e: GeminiAuthException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Gemini stream attempt failed for $model", e)
                lastException = e
                if (accumulated.isNotEmpty()) {
                    throw e
                }
            }
        }

        if (!succeeded) {
            throw (lastException ?: IllegalStateException("Could not reach Gemini right now."))
        }
    }.flowOn(Dispatchers.IO)

    private fun extractCandidateText(jsonLine: String): String {
        return try {
            val root = JSONObject(jsonLine)
            val candidates = root.optJSONArray("candidates") ?: return ""
            if (candidates.length() == 0) return ""
            val parts = candidates.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts") ?: return ""
            buildString {
                for (i in 0 until parts.length()) {
                    val part = parts.optJSONObject(i) ?: continue
                    val text = part.optString("text", "")
                    if (text.isNotEmpty()) append(text)
                }
            }
        } catch (_: Exception) {
            ""
        }
    }

    private fun parseApiErrorMessage(body: String): String {
        if (body.isBlank()) return ""
        return try {
            JSONObject(body).optJSONObject("error")?.optString("message", "").orEmpty()
        } catch (_: Exception) {
            body.take(200)
        }
    }

    private companion object {
        const val TAG = "GeminiTutorRepo"
        const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
        val MODEL_CANDIDATES = listOf(
            "gemini-3.8-flash",
            "gemini-3.5-flash-lite",
            "gemini-2.5-flash"
        )
    }
}
