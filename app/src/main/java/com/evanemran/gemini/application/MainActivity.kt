package com.evanemran.gemini.application

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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.evanemran.gemini.R
import com.evanemran.gemini.adapters.DrawerAdapter
import com.evanemran.gemini.adapters.MessageListAdapter
import com.evanemran.gemini.config.ChatType
import com.evanemran.gemini.databinding.ActivityMainBinding
import com.evanemran.gemini.listeners.ClickListener
import com.evanemran.gemini.listeners.GeminiResponseListener
import com.evanemran.gemini.model.DrawerMenu
import com.evanemran.gemini.model.MessageModel
import com.evanemran.gemini.utils.BitmapUtils
import com.evanemran.gemini.utils.CustomTypefaceSpan
import com.evanemran.gemini.utils.GeminiPromptManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private lateinit var binding: ActivityMainBinding
    private var selectedFragment: Fragment = TextFragment()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        replaceFragment(selectedFragment)

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
        val drawerAdapter = DrawerAdapter(this, navMenus, drawerClickListener)
        binding.recyclerNav.adapter = drawerAdapter
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private fun dispatchPickImageIntent() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickIntent, REQUEST_IMAGE_PICK)
    }

    private val drawerClickListener: ClickListener<DrawerMenu> = object : ClickListener<DrawerMenu>{
        override fun onClicked(data: DrawerMenu) {
            when (data) {
                DrawerMenu.TEXT -> {
                    if (selectedFragment !is TextFragment){
                        selectedFragment = TextFragment()
                        replaceFragment(TextFragment())
                    }
                }
                DrawerMenu.VOICE -> {
                    if (selectedFragment !is VoiceFragment){
                        selectedFragment = VoiceFragment()
                        replaceFragment(VoiceFragment())
                    }
                }
                DrawerMenu.API_KEY -> {
                    if (selectedFragment !is VoiceFragment){
                        selectedFragment = VoiceFragment()
                        replaceFragment(VoiceFragment())
                    }
                }
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

    }
}