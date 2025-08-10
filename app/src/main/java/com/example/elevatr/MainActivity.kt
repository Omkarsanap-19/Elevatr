package com.example.elevatr

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.elevatr.frags.CoverFragment
import com.example.elevatr.frags.HomeFragment
import com.example.elevatr.frags.ProfileFragment
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import me.ibrahimsn.lib.SmoothBottomBar

class MainActivity : AppCompatActivity() {
    private lateinit var bottomBar: SmoothBottomBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bottomBar = findViewById(R.id.bottomBar)

        PDFBoxResourceLoader.init(applicationContext)

        // Load default fragment
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // Handle bottom nav clicks
        bottomBar.onItemSelected = { position ->
            when (position) {
                0 -> replaceFragment(HomeFragment())
                1 -> replaceFragment(CoverFragment())
                2 -> replaceFragment(ProfileFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}