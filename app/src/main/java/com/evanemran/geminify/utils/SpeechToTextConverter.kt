package com.evanemran.geminify.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import java.util.Locale

class SpeechToTextConverter(private val activity: Activity) {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textToSpeech: TextToSpeech
    private var onTextRecognizedListener: OnTextRecognizedListener? = null

    interface OnTextRecognizedListener {
        fun onTextRecognized(text: String)
        fun onError()
    }

    fun setOnTextRecognizedListener(listener: OnTextRecognizedListener) {
        this.onTextRecognizedListener = listener
    }

    fun speechToText() {
        initSpeechRecognizer()
        startListening()
    }

    fun textToSpeech(text: String) {
        initializeTextToSpeech()
        speakText(text)
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(activity)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
            speechRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    // Handle recognition error
                    onTextRecognizedListener?.onError()
                }

                override fun onResults(results: Bundle?) {
                    val matches: ArrayList<String>? =
                        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    // Use the recognized text from matches[0]
                    if (!matches.isNullOrEmpty()) {
                        val recognizedText = matches[0]
                        // Notify the listener with the recognized text
                        onTextRecognizedListener?.onTextRecognized(recognizedText)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    private fun startListening() {
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizer.startListening(speechIntent)
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(activity) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // TextToSpeech engine initialized successfully
                print("TTS Success")
            } else {
                // Handle initialization error
                print("TTS Failed")
            }
        }
    }

    private fun speakText(text: String) {
        /*val locale = Locale.US
        val languageCode = locale.language
        val country = locale.country

        val result = textToSpeech.isLanguageAvailable(Locale(languageCode, country))
        if (result == TextToSpeech.LANG_AVAILABLE) {
            textToSpeech.language = Locale(languageCode, country)
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            // Handle language not available
            Log.e("SpeechToTextConverter", "Desired language not available")
        }*/
        val locale = Locale("en", "US")
        textToSpeech.language = locale
        print(textToSpeech.availableLanguages)
        if (textToSpeech.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            // Handle language not available
            print("Locale not matched")
        }
    }

    fun shutdownTextToSpeech() {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
}