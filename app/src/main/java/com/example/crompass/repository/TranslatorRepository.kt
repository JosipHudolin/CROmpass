package com.example.crompass.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.crompass.BuildConfig
import com.example.crompass.model.TranslationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

private val Context.recentTranslationsDataStore by preferencesDataStore(name = "recent_translations")
private val RECENT_KEY = stringPreferencesKey("recent_json")

class TranslatorRepository(private val context: Context) {

    val apiKey = BuildConfig.GOOGLE_API_KEY
    private val client = OkHttpClient()

    fun translate(text: String, targetLanguage: String): TranslationResult {
        val url = "https://translation.googleapis.com/language/translate/v2?key=$apiKey"

        val json = JSONObject()
        json.put("q", text)
        json.put("target", targetLanguage)

        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = client.newCall(request).execute()

        var detectedSourceLanguage = "unknown"

        val translatedText = if (response.isSuccessful) {
            val responseBody = response.body?.string()
            val jsonObject = JSONObject(responseBody)

            val translationObject = jsonObject
                .getJSONObject("data")
                .getJSONArray("translations")
                .getJSONObject(0)

            detectedSourceLanguage = translationObject.optString("detectedSourceLanguage", "unknown")

            translationObject.getString("translatedText")
        } else {
            "$text [translation failed]"
        }

        return TranslationResult(
            originalText = text,
            translatedText = translatedText,
            sourceLanguageCode = detectedSourceLanguage,
            targetLanguageCode = targetLanguage
        )
    }

    fun getRecentTranslations(): Flow<List<TranslationResult>> {
        return context.recentTranslationsDataStore.data.map { preferences ->
            val jsonString = preferences[RECENT_KEY] ?: "[]"
            val jsonArray = JSONArray(jsonString)
            jsonArrayToList(jsonArray)
        }
    }

    suspend fun addRecent(result: TranslationResult, maxItems: Int = 5) {
        context.recentTranslationsDataStore.edit { preferences ->
            val currentJson = preferences[RECENT_KEY] ?: "[]"
            val jsonArray = JSONArray(currentJson)
            // Insert new result at start
            val newArray = JSONArray()
            newArray.put(translationResultToJson(result))
            for (i in 0 until jsonArray.length()) {
                if (newArray.length() >= maxItems) break
                newArray.put(jsonArray.getJSONObject(i))
            }
            preferences[RECENT_KEY] = newArray.toString()
        }
    }

    suspend fun clearRecent() {
        context.recentTranslationsDataStore.edit { preferences ->
            preferences.remove(RECENT_KEY)
        }
    }

    private fun jsonArrayToList(jsonArray: JSONArray): List<TranslationResult> {
        val list = mutableListOf<TranslationResult>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.optJSONObject(i)
            if (obj != null) {
                list.add(jsonToTranslationResult(obj))
            }
        }
        return list
    }

    private fun jsonToTranslationResult(json: JSONObject): TranslationResult {
        return TranslationResult(
            originalText = json.optString("originalText"),
            translatedText = json.optString("translatedText"),
            sourceLanguageCode = json.optString("sourceLanguageCode"),
            targetLanguageCode = json.optString("targetLanguageCode")
        )
    }

    private fun translationResultToJson(result: TranslationResult): JSONObject {
        val json = JSONObject()
        json.put("originalText", result.originalText)
        json.put("translatedText", result.translatedText)
        json.put("sourceLanguageCode", result.sourceLanguageCode)
        json.put("targetLanguageCode", result.targetLanguageCode)
        return json
    }
}