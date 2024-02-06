package com.evanemran.gemini.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.evanemran.gemini.adapters.MessageListAdapter
import com.evanemran.gemini.config.BuildConfig
import com.evanemran.gemini.config.ChatType
import com.evanemran.gemini.databinding.ActivityVoiceBinding
import com.evanemran.gemini.model.MessageModel
import com.evanemran.gemini.utils.PermissionUtils
import com.evanemran.gemini.utils.SpeechToTextConverter
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class VoiceActivity : AppCompatActivity() {

    private lateinit var speechToTextConverter: SpeechToTextConverter
    private lateinit var binding: ActivityVoiceBinding
    var messageList: MutableList<MessageModel> = mutableListOf()
    private var adapter: MessageListAdapter? = null
    private val apiKey = BuildConfig.apiKey
    private val generativeModel = GenerativeModel(
        // Use a model that's applicable for your use case (see "Implement basic use cases" below)
        modelName = "gemini-pro",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = apiKey
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        messageList.add(MessageModel("Hi! I am Gemini, How may I assist you?", "NA", mIsReply = false, mIsImagePrompt = false, null))

        binding.recyclerVoiceChat.setHasFixedSize(true)
        binding.recyclerVoiceChat.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        adapter = MessageListAdapter(this, messageList, ChatType.VOICE)
        binding.recyclerVoiceChat.adapter = adapter

        PermissionUtils.requestPermission(
            this,
            PermissionUtils.PERMISSION_RECORD_AUDIO,
            "This app needs access to mic to read your voice."
        ) { // Callback when permission is granted
            // Do something with storage
            Toast.makeText(this, "Have Permission", Toast.LENGTH_SHORT).show()

            val chat = generativeModel.startChat(
                history = listOf(
                    content(role = "user") { text("Hello, I am Evan. I am a software engineer") },
                    content(role = "model") { text("Great to meet you. What would you like to know?") }
                )
            )

            speechToTextConverter = SpeechToTextConverter(this)
            speechToTextConverter.textToSpeech("Hello! This is a simulation of Text to Speech")
            speechToTextConverter.setOnTextRecognizedListener(object :
                SpeechToTextConverter.OnTextRecognizedListener {
                override fun onTextRecognized(text: String) {
                    // Handle the recognized text
                    // Update UI or perform any action
                    binding.textViewListenStatus.visibility = View.GONE
                    messageList.add(MessageModel(text, "NA", mIsReply = false, mIsImagePrompt = false, null))
                    adapter!!.notifyDataSetChanged()
                    lifecycleScope.launch {
                        val response = chat.sendMessage(text)
                        messageList.add(MessageModel(response.text.toString().trim(), "NA", mIsReply = true, mIsImagePrompt = true, null))
                        adapter!!.notifyDataSetChanged()
                        binding.recyclerVoiceChat.smoothScrollToPosition(messageList.size-1)
                        speechToTextConverter.textToSpeech(response.text.toString().trim())
                    }
                }

                override fun onError() {
                    // Handle recognition error
                    binding.textViewListenStatus.visibility = View.GONE
                    runOnUiThread {
//                        binding.textToSpeech.text = "Error"
                    }
                }
            })

            binding.fabRecord.setOnClickListener {
                // Call this method when you want to initiate voice input
                binding.textViewListenStatus.visibility = View.VISIBLE
                speechToTextConverter.speechToText()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode, grantResults) {
            // Handle permission granting here
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
        }
    }
}