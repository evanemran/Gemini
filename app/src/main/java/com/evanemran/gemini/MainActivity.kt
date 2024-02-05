package com.evanemran.gemini

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.evanemran.gemini.adapters.MessageListAdapter
import com.evanemran.gemini.config.BuildConfig
import com.evanemran.gemini.config.ChatType
import com.evanemran.gemini.databinding.ActivityMainBinding
import com.evanemran.gemini.model.MessageModel
import com.evanemran.gemini.utils.BitmapUtils
import com.evanemran.gemini.utils.CustomTypefaceSpan
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private lateinit var binding: ActivityMainBinding
    private var adapter: MessageListAdapter? = null
    var messageList: MutableList<MessageModel> = mutableListOf()

    private var selectedImageBitmap: Bitmap? = null

    private val apiKey = BuildConfig().apiKey
    private val generativeModel = GenerativeModel(
        // Use a model that's applicable for your use case (see "Implement basic use cases" below)
        modelName = "gemini-pro",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = apiKey
    )

    val generativeImageModel = GenerativeModel(
        // For text-and-images input (multimodal), use the gemini-pro-vision model
        modelName = "gemini-pro-vision",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig().apiKey
    )
//    val textView: TextView = findViewById(R.id.textView)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)
        val customTypeface: Typeface? =
            ResourcesCompat.getFont(this, R.font.custom_font_bold)

        if (customTypeface != null) {
            val title = getString(R.string.app_name)
            val spannableString = SpannableString(title)
            spannableString.setSpan(
                CustomTypefaceSpan(customTypeface),
                0,
                title.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            supportActionBar?.title = spannableString
        }
        messageList.add(MessageModel("Hi! I am Gemini, How may I assist you?", "NA", mIsReply = false, mIsImagePrompt = false, null))

        binding.chatList.setHasFixedSize(true)
        binding.chatList.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        adapter = MessageListAdapter(this, messageList, ChatType.TEXT)
        binding.chatList.adapter = adapter

        val chat = generativeModel.startChat(
            history = listOf(
                content(role = "user") { text("Hello, I am Evan. I am a software engineer") },
                content(role = "model") { text("Great to meet you. What would you like to know?") }
            )
        )

        binding.commandLine.setOnTouchListener(OnTouchListener { _, event ->
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
                adapter!!.notifyDataSetChanged()
                binding.progressbar.visibility = View.VISIBLE
                binding.run.visibility = View.GONE
                lifecycleScope.launch {

                    if(selectedImageBitmap!=null) {
                        if(prompt.isNotEmpty()) {
                            messageList.add(MessageModel(prompt, "NA", mIsReply = false, mIsImagePrompt = true, selectedImageBitmap!!))
                            binding.pickedImageView.visibility = View.GONE
                            val inputContent = content {
                                image(selectedImageBitmap!!)
                                text(prompt)
                            }

                            val response = generativeImageModel.generateContent(inputContent)
                            messageList.add(MessageModel(response.text.toString().trim(), "NA", mIsReply = true, mIsImagePrompt = true, selectedImageBitmap!!))
                            selectedImageBitmap = null
                        }
                        else {
                            binding.commandLine.error = "Enter Prompt."
                        }
                    }
                    else {
                        messageList.add(MessageModel(prompt, "NA", mIsReply = false, mIsImagePrompt = false, null))
                        val response = chat.sendMessage(prompt)
                        messageList.add(MessageModel(response.text.toString().trim(), "NA", mIsReply = true, mIsImagePrompt = true, null))

                        //For single thread chat
                        //val response = generativeModel.generateContent(prompt)
                        print(response.text)
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
        startActivityForResult(pickIntent, REQUEST_IMAGE_PICK)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    binding.pickedImageView.visibility = View.VISIBLE
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.pickedImageView.setImageBitmap(imageBitmap)
                    selectedImageBitmap = imageBitmap
                }
                REQUEST_IMAGE_PICK -> {
                    binding.pickedImageView.visibility = View.VISIBLE
                    val imageUri = data?.data
                    val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    binding.pickedImageView.setImageBitmap(imageBitmap)
                    selectedImageBitmap = BitmapUtils().compressBitmap(imageBitmap)
                }
            }
        }
    }
}