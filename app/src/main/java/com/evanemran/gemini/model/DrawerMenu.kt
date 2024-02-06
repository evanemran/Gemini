package com.evanemran.gemini.model

import androidx.fragment.app.Fragment
import com.evanemran.gemini.application.SettingsFragment
import com.evanemran.gemini.application.TextFragment
import com.evanemran.gemini.application.VoiceFragment

enum class DrawerMenu(title: String, subTitle: String, fragment: Fragment) {
    TEXT("Text AI", "Chat with Gemini", TextFragment()),
    VOICE("Voice AI", "Talk with Gemini", VoiceFragment()),
    API_KEY("API Key", "Your API Key", SettingsFragment());

    var title: String = title
    var subTitle: String = subTitle
    var fragment: Fragment = fragment
}