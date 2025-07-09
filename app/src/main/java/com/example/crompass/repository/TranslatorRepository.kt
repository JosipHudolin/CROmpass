package com.example.crompass.repository

import com.example.crompass.model.TranslationResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class TranslatorRepository {

    private val apiKey = "AIzaSyAsjpoWGgWkxssVGB1qrM7WhhI8je5LrdI"  // Replace this with your real key
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
}