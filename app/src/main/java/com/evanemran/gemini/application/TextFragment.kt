package com.evanemran.gemini.application

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.evanemran.gemini.adapters.MessageListAdapter
import com.evanemran.gemini.config.BuildConfig
import com.evanemran.gemini.config.ChatType
import com.evanemran.gemini.databinding.FragmentTextBinding
import com.evanemran.gemini.listeners.GeminiResponseListener
import com.evanemran.gemini.model.MessageModel
import com.evanemran.gemini.utils.BitmapUtils
import com.evanemran.gemini.utils.GeminiPromptManager
import kotlinx.coroutines.launch

class TextFragment: Fragment() {

    private lateinit var geminiPromptManager: GeminiPromptManager
    private var adapter: MessageListAdapter? = null
    var messageList: MutableList<MessageModel> = mutableListOf()
    private lateinit var binding: FragmentTextBinding

    private var selectedImageBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geminiPromptManager = GeminiPromptManager(requireContext(), geminiResponseListener)

        messageList.add(MessageModel("Hi! I am Gemini, How may I assist you?", "NA", mIsReply = false, mIsImagePrompt = false, null))

        binding.chatList.setHasFixedSize(true)
        binding.chatList.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        adapter = MessageListAdapter(requireContext(), messageList, ChatType.TEXT)
        binding.chatList.adapter = adapter

        binding.commandLine.setOnTouchListener(View.OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.commandLine.right - binding.commandLine.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                ) {
                    dispatchPickImageIntent()
                    return@OnTouchListener true
                }
            }
            false
        })

        binding.run.setOnClickListener {
            val prompt = binding.commandLine.text.toString()
            if(prompt.isNotEmpty()) {
                binding.commandLine.setText("")
                binding.progressbar.visibility = View.VISIBLE
                binding.run.visibility = View.GONE
                lifecycleScope.launch {

                    if(selectedImageBitmap!=null) {
                        if(prompt.isNotEmpty()) {
                            binding.pickedImageView.visibility = View.GONE
                            messageList.add(MessageModel(prompt, "NA", mIsReply = false, mIsImagePrompt = true, selectedImageBitmap!!))
                            geminiPromptManager.askWithImage(prompt, selectedImageBitmap!!)
                        }
                        else {
                            binding.commandLine.error = "Enter Prompt."
                        }
                    }
                    else {
                        messageList.add(MessageModel(prompt, "NA", mIsReply = false, mIsImagePrompt = false, null))
                        geminiPromptManager.askWithText(prompt)
                    }

                    binding.progressbar.visibility = View.GONE
                    binding.run.visibility = View.VISIBLE
                    adapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    private fun dispatchPickImageIntent() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickIntent, BuildConfig.REQUEST_IMAGE_PICK)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                BuildConfig.REQUEST_IMAGE_CAPTURE -> {
                    binding.pickedImageView.visibility = View.VISIBLE
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.pickedImageView.setImageBitmap(imageBitmap)
                    selectedImageBitmap = imageBitmap
                }
                BuildConfig.REQUEST_IMAGE_PICK -> {
                    binding.pickedImageView.visibility = View.VISIBLE
                    val imageUri = data?.data
                    val imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                    binding.pickedImageView.setImageBitmap(imageBitmap)
                    selectedImageBitmap = BitmapUtils.compressBitmap(imageBitmap)
                }
            }
        }
    }

    private val geminiResponseListener: GeminiResponseListener = object : GeminiResponseListener {
        override fun onResponse(response: String, isImage: Boolean) {
            messageList.add(MessageModel(response.trim(), "NA", mIsReply = true, mIsImagePrompt = isImage, selectedImageBitmap))
            selectedImageBitmap = null
        }

    }
}