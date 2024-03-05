package com.evanemran.geminify.listeners

interface GeminiResponseListener {
    fun onResponse(response: String, isImage: Boolean)
}