package com.example.biblio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.biblio.databinding.ActivitySettingsBinding
import com.example.biblio.fragments.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment())
                .commit()
    }
}