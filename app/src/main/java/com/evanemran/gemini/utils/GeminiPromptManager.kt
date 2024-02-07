package com.evanemran.gemini.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import com.evanemran.gemini.config.BuildConfig
import com.evanemran.gemini.listeners.GeminiResponseListener
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class GeminiPromptManager(private val activity: Context, private val responseListener: GeminiResponseListener) {

    private val apiKeyManager = ApiKeyManager(activity)

    private val generativeModel = GenerativeModel(
        // Use a model that's applicable for your use case (see "Implement basic use cases" below)
        modelName = "gemini-pro",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = apiKeyManager.apiKey.let {
            if(it.isNullOrEmpty()) {
                return@let ""
            }
            else {
                return@let it
            }
        }
    )

    private val generativeImageModel = GenerativeModel(
        // For text-and-images input (multimodal), use the gemini-pro-vision model
        modelName = "gemini-pro-vision",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = apiKeyManager.apiKey.let {
            if(it.isNullOrEmpty()) {
                return@let ""
            }
            else {
                return@let it
            }
        }
    )

    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "user") { text("Hello, I am Evan. I am a software engineer") },
            content(role = "model") { text("Great to meet you. What would you like to know?") }
        )
    )

    suspend fun askWithText(prompt: String) {
        val response = try {
            chat.sendMessage(prompt).text.toString()
        }catch (e: Exception) {
            "Something went wrong! Please try again"
        }
        responseListener.onResponse(response, isImage = false)

        //For single thread chat
        //val response = generativeModel.generateContent(prompt)
    }

    suspend fun askWithImage(prompt: String, image: Bitmap) {
        val inputContent = content {
            image(image)
            text(prompt)
        }

        val response = try {
            generativeImageModel.generateContent(inputContent).text.toString()
        }catch (e: Exception) {
            "Something went wrong! Please try again"
        }
        responseListener.onResponse(response, isImage = true)
    }
}