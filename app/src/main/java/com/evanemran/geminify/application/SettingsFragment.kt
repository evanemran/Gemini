package com.evanemran.geminify.application

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.evanemran.geminify.databinding.FragmentSettingsBinding
import com.evanemran.geminify.utils.ApiKeyManager

class SettingsFragment: Fragment() {


    private lateinit var binding: FragmentSettingsBinding
    private lateinit var apiKeyManager: ApiKeyManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiKeyManager = ApiKeyManager(requireContext())

        apiKeyManager.apiKey.let {
            if (!it.isNullOrEmpty()) {
                binding.apiKeyField.setText(it)
            }
        }


        binding.buttonSave.setOnClickListener {
            if(binding.apiKeyField.text.toString().isNotEmpty()) {
                apiKeyManager.clearApiKey()
                apiKeyManager.apiKey = binding.apiKeyField.text.toString()
                Toast.makeText(requireContext(), "Saved API Key", Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonGetApiKey.setOnClickListener {
            openUrlInBrowser(requireContext(), "https://aistudio.google.com/app/apikey")
        }
    }

    fun openUrlInBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}