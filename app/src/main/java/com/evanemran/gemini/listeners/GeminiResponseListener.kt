package com.evanemran.gemini.listeners

interface GeminiResponseListener {
    fun onResponse(response: String, isImage: Boolean)
}