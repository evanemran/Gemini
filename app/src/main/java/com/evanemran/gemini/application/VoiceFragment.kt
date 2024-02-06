package com.evanemran.gemini.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.evanemran.gemini.R
import com.evanemran.gemini.adapters.MessageListAdapter
import com.evanemran.gemini.config.ChatType
import com.evanemran.gemini.databinding.ActivityVoiceBinding
import com.evanemran.gemini.listeners.GeminiResponseListener
import com.evanemran.gemini.model.MessageModel
import com.evanemran.gemini.utils.GeminiPromptManager
import com.evanemran.gemini.utils.PermissionUtils
import com.evanemran.gemini.utils.SpeechToTextConverter
import kotlinx.coroutines.launch

class VoiceFragment: Fragment() {

    private lateinit var speechToTextConverter: SpeechToTextConverter
    private lateinit var geminiPromptManager: GeminiPromptManager
    private lateinit var binding: ActivityVoiceBinding
    var messageList: MutableList<MessageModel> = mutableListOf()
    private var adapter: MessageListAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityVoiceBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geminiPromptManager = GeminiPromptManager(requireContext(), geminiResponseListener)

        messageList.add(MessageModel("Hi! I am Gemini, How may I assist you?", "NA", mIsReply = false, mIsImagePrompt = false, null))

        binding.recyclerVoiceChat.setHasFixedSize(true)
        binding.recyclerVoiceChat.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        adapter = MessageListAdapter(requireContext(), messageList, ChatType.VOICE)
        binding.recyclerVoiceChat.adapter = adapter

        PermissionUtils.requestPermission(
            requireActivity(),
            PermissionUtils.PERMISSION_RECORD_AUDIO,
            "This App Needs Access to Mic to Read Your Voice."
        ) { // Callback when permission is granted

            speechToTextConverter = SpeechToTextConverter(requireActivity())
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
                }
            })

            binding.fabRecord.setOnClickListener {
                // Call this method when you want to initiate voice input
                binding.textViewListenStatus.visibility = View.VISIBLE
                speechToTextConverter.speechToText()
            }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode, grantResults) {
            // Handle permission granting here
            Toast.makeText(requireContext(), "Granted", Toast.LENGTH_SHORT).show()
        }
    }
}