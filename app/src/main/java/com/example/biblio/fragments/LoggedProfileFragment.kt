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
        val binding = FragmentLoggedProfileBinding.inflate(inflater, container, false)
        val current = getCurrentUser(context!!)

        binding.loggedUsernameTv.text = current.username
        binding.loggedEmailTv.text = current.email
        binding.loggedDownloadTv.text = current.totalDownloads.toString()
        binding.loggedReviewsTv.text = current.totalReviews.toString()

        if (current.photoUri != null) Glide.with(context!!).load(current.photoUri)
                .placeholder(R.drawable.account_circle_outline)
                .apply(RequestOptions().centerInside().circleCrop())
                .into(binding.loggedPhoto)

        val lastSearch = getLastSearchTS(context!!)
        if (lastSearch != null) binding.loggedLastSearchTv.text = PrettyTime().format(lastSearch)
        binding.logoutBtn.setOnClickListener {
            removeCurrentUser(context!!)
            replaceWith(ProfileFragment())
        }

        binding.loggedSettingsBtn.setOnClickListener { startActivity(Intent(activity, SettingsActivity::class.java)) }
        return binding.root
    }
}