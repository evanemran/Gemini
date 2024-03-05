package com.evanemran.geminify.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.evanemran.geminify.adapters.MessageListAdapter
import com.evanemran.geminify.config.ChatType
import com.evanemran.geminify.databinding.ActivityVoiceBinding
import com.evanemran.geminify.listeners.GeminiResponseListener
import com.evanemran.geminify.model.MessageModel
import com.evanemran.geminify.utils.GeminiPromptManager
import com.evanemran.geminify.utils.PermissionUtils
import com.evanemran.geminify.utils.SpeechToTextConverter
import kotlinx.coroutines.launch

class VoiceActivity : AppCompatActivity() {

    private lateinit var speechToTextConverter: SpeechToTextConverter
    private lateinit var geminiPromptManager: GeminiPromptManager
    private lateinit var binding: ActivityVoiceBinding
    var messageList: MutableList<MessageModel> = mutableListOf()
    private var adapter: MessageListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        geminiPromptManager = GeminiPromptManager(this, geminiResponseListener)

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
                        geminiPromptManager.askWithText(text)
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

    private val geminiResponseListener: GeminiResponseListener = object : GeminiResponseListener {
        override fun onResponse(response: String, isImage: Boolean) {
            messageList.add(MessageModel(response.trim(), "NA", mIsReply = true, mIsImagePrompt = true, null))
            adapter!!.notifyDataSetChanged()
            binding.recyclerVoiceChat.smoothScrollToPosition(messageList.size-1)
            speechToTextConverter.textToSpeech(response.trim())
        }

    }
}