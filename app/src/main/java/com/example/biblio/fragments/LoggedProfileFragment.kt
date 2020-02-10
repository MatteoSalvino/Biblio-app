package com.example.biblio.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biblio.R
import com.example.biblio.SettingsActivity
import com.example.biblio.databinding.FragmentLoggedProfileBinding
import com.example.biblio.helpers.SimpleBiblioHelper.getCurrentUser
import com.example.biblio.helpers.SimpleBiblioHelper.getLastSearchTS
import com.example.biblio.helpers.SimpleBiblioHelper.removeCurrentUser
import com.example.biblio.helpers.XFragment
import org.ocpsoft.prettytime.PrettyTime

class LoggedProfileFragment : XFragment(LoggedProfileFragment::class.java) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = FragmentLoggedProfileBinding.inflate(inflater, container, false)
        val user = getCurrentUser(xContext)

        binding.loggedUsernameTv.text = user.username
        binding.loggedEmailTv.text = user.email
        binding.loggedDownloadTv.text = user.totalDownloads.toString()
        binding.loggedReviewsTv.text = user.totalReviews.toString()

        if (user.photoUri != null) Glide.with(xContext).load(user.photoUri)
                .placeholder(R.drawable.account_circle_outline)
                .apply(RequestOptions().centerInside().circleCrop())
                .into(binding.loggedPhoto)

        val lastSearch = getLastSearchTS(xContext)
        if (lastSearch != null) binding.loggedLastSearchTv.text = PrettyTime().format(lastSearch)
        binding.logoutBtn.setOnClickListener {
            removeCurrentUser(xContext)
            replaceWith(ProfileFragment())
        }

        binding.loggedSettingsBtn.setOnClickListener { startActivity(Intent(activity, SettingsActivity::class.java)) }
        return binding.root
    }
}