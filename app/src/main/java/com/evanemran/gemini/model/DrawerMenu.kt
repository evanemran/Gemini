package com.evanemran.gemini.model

enum class DrawerMenu(title: String, subTitle: String) {
    TEXT("Text AI", "Chat with Gemini"),
    VOICE("Voice AI", "Talk with Gemini"),
    API_KEY("API Key", "Your API Key");

    var title: String = title
    var subTitle: String = subTitle
}