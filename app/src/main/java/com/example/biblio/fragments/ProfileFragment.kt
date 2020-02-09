package com.example.biblio.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.biblio.LoginActivity
import com.example.biblio.SettingsActivity
import com.example.biblio.databinding.FragmentProfileBinding
import com.example.biblio.helpers.XFragment

class ProfileFragment : XFragment(ProfileFragment::class.java) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.emailLoginBtn.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivityForResult(intent, RC_SIGN_IN)
        }
        binding.settingsBtn.setOnClickListener { startActivity(Intent(activity, SettingsActivity::class.java)) }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) replaceWith(LoggedProfileFragment())
    }

    companion object {
        private const val RC_SIGN_IN = 1
    }
}