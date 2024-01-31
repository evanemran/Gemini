package com.evanemran.gemini

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.evanemran.gemini.adapters.MessageListAdapter
import com.evanemran.gemini.config.BuildConfig
import com.evanemran.gemini.databinding.ActivityMainBinding
import com.evanemran.gemini.model.MessageModel
import com.evanemran.gemini.utils.CustomTypefaceSpan
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var adapter: MessageListAdapter? = null
    var messageList: MutableList<MessageModel> = mutableListOf()

    val apiKey = BuildConfig().apiKey
    val generativeModel = GenerativeModel(
        // Use a model that's applicable for your use case (see "Implement basic use cases" below)
        modelName = "gemini-pro",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = apiKey
    )
//    val textView: TextView = findViewById(R.id.textView)

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
        messageList.add(MessageModel("Hi! I am Gemini, How may I assist you?", "NA", true))

        binding.chatList.setHasFixedSize(true)
        binding.chatList.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        adapter = MessageListAdapter(this, messageList)
        binding.chatList.adapter = adapter

        binding.run.setOnClickListener {
            val prompt = binding.commandLine.text.toString()
            messageList.add(MessageModel(prompt, "NA", false))
            adapter!!.notifyDataSetChanged()
            binding.progressbar.visibility = View.VISIBLE
            binding.run.visibility = View.GONE
            lifecycleScope.launch {
                val response = generativeModel.generateContent(prompt)
                print(response.text)
                messageList.add(MessageModel(response.text.toString(), "NA", true))
                binding.progressbar.visibility = View.GONE
                binding.run.visibility = View.VISIBLE
                binding.commandLine.setText("")
                adapter!!.notifyDataSetChanged()
            }
        }
    }
}