package com.evanemran.geminify.application

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.evanemran.geminify.R
import com.evanemran.geminify.adapters.DrawerAdapter
import com.evanemran.geminify.databinding.ActivityMainBinding
import com.evanemran.geminify.listeners.ClickListener
import com.evanemran.geminify.model.DrawerMenu
import com.evanemran.geminify.utils.ApiKeyManager
import com.evanemran.geminify.utils.CustomTypefaceSpan

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerAdapter: DrawerAdapter
    private lateinit var selectedDrawerMenu: DrawerMenu
    private lateinit var apiKeyManager: ApiKeyManager

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        apiKeyManager = ApiKeyManager(this)
        selectedDrawerMenu = apiKeyManager.apiKey.let {
            if(it.isNullOrEmpty()) {
                return@let DrawerMenu.API_KEY
            }
            else {
                return@let DrawerMenu.TEXT
            }
        }
        replaceFragment(selectedDrawerMenu.fragment)

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

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupNavMenu()
    }

    private fun setupNavMenu() {
        val navMenus: MutableList<DrawerMenu> = mutableListOf()
        navMenus.add(DrawerMenu.TEXT)
        navMenus.add(DrawerMenu.VOICE)
        navMenus.add(DrawerMenu.API_KEY)

        binding.recyclerNav.setHasFixedSize(true)
        binding.recyclerNav.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        drawerAdapter = DrawerAdapter(this, navMenus, drawerClickListener, selectedDrawerMenu)
        binding.recyclerNav.adapter = drawerAdapter
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private val drawerClickListener: ClickListener<DrawerMenu> = object : ClickListener<DrawerMenu>{
        override fun onClicked(data: DrawerMenu) {
            when (data) {
                DrawerMenu.TEXT -> {
                    apiKeyManager.apiKey.let {
                        if(it.isNullOrEmpty()) {
                            Toast.makeText(this@MainActivity, "Add API Key First!", Toast.LENGTH_SHORT).show()
                            return
                        }
                        else {
                            if (selectedDrawerMenu.fragment !is TextFragment){
                                selectedDrawerMenu = DrawerMenu.TEXT
                                replaceFragment(TextFragment())
                            }
                        }
                    }
                }
                DrawerMenu.VOICE -> {
                    apiKeyManager.apiKey.let {
                        if(it.isNullOrEmpty()) {
                            Toast.makeText(this@MainActivity, "Add API Key First!", Toast.LENGTH_SHORT).show()
                            return
                        }
                        else {
                            if (selectedDrawerMenu.fragment !is VoiceFragment){
                                selectedDrawerMenu = DrawerMenu.VOICE
                                replaceFragment(VoiceFragment())
                            }
                        }
                    }
                }
                DrawerMenu.API_KEY -> {
                    if (selectedDrawerMenu.fragment !is SettingsFragment){
                        selectedDrawerMenu = DrawerMenu.API_KEY
                        replaceFragment(SettingsFragment())
                    }
                }
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

    }
}