package com.evanemran.gemini.utils

import android.content.Context
import android.content.SharedPreferences

class ApiKeyManager(context: Context) {

    companion object {
        private const val PREF_NAME = "ApiPreferences"
        private const val API_KEY_KEY = "apiKey"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var apiKey: String?
        get() = sharedPreferences.getString(API_KEY_KEY, null)
        set(value) {
            sharedPreferences.edit().putString(API_KEY_KEY, value).apply()
        }

    fun clearApiKey() {
        sharedPreferences.edit().remove(API_KEY_KEY).apply()
    }
}
