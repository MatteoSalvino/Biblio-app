package com.example.biblio.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblio.R
import com.example.biblio.adapters.ReviewsAdapter
import com.example.biblio.api.Review
import com.example.biblio.api.SimpleBiblioCommons
import com.example.biblio.api.User
import com.example.biblio.databinding.ReviewsFragmentBinding
import com.example.biblio.helpers.SimpleBiblioHelper.getCurrentUser
import com.example.biblio.helpers.SimpleBiblioHelper.setCurrentUser
import com.example.biblio.helpers.XFragment
import com.example.biblio.viewmodels.EbookDetailsViewModel
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lrusso96.simplebiblio.core.Ebook
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import me.zhanghai.android.materialratingbar.MaterialRatingBar.OnRatingChangeListener

class ReviewsFragment : XFragment(ReviewsFragment::class.java) {
    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var binding: ReviewsFragmentBinding
    private lateinit var mAdapter: ReviewsAdapter
    private lateinit var mEbook: Ebook
    private var user: User? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = ReviewsFragmentBinding.inflate(inflater, container, false)
        val appbarBinding = binding.appbar
        val model = ViewModelProvider(activity!!).get(EbookDetailsViewModel::class.java)
        mEbook = model.ebook.value!!
        user = getCurrentUser(xContext)

        // Show reviews button to logged-in users only
        val visibility = if (user == null) View.INVISIBLE else View.VISIBLE
        appbarBinding.reviewsAddBtn.visibility = visibility

        //Initialize AlertDialog to post a new review
        val builder = AlertDialog.Builder(activity!!)
        val dialogView = inflater.inflate(R.layout.progress_review, null)
        builder.setView(dialogView)
        val reviewBody = dialogView.findViewById<EditText>(R.id.review_body)
        val ratingBar: MaterialRatingBar = dialogView.findViewById(R.id.review_rating)
        val postBtn: MaterialButton = dialogView.findViewById(R.id.review_post_btn)
        postBtn.isEnabled = false
        val alertDialog = builder.create()
        reviewBody.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                postBtn.isEnabled = editable.toString().length >= 10 && ratingBar.rating > 0
            }
        })
        ratingBar.onRatingChangeListener = OnRatingChangeListener { bar: MaterialRatingBar, _: Float -> postBtn.isEnabled = bar.rating > 0 && reviewBody.text.toString().length >= 10 }
        postBtn.setOnClickListener {
            val text = reviewBody.text.toString()
            val _user = user
            val rating = ratingBar.rating.toInt()
            if (_user != null) uiScope.launch { rate(_user, text, rating) }
            alertDialog.dismiss()
            //Clean dialog's fields
            reviewBody.setText("")
            ratingBar.rating = 0f
        }
        appbarBinding.backBtn.setOnClickListener { popBackStackImmediate() }
        appbarBinding.reviewsAddBtn.setOnClickListener { alertDialog.show() }
        val mLayoutManager = LinearLayoutManager(context)
        binding.ebooksRv.layoutManager = mLayoutManager
        binding.ebooksRv.setHasFixedSize(true)
        mAdapter = ReviewsAdapter()
        binding.ebooksRv.adapter = mAdapter
        retrieveReviews()
        return binding.root
    }

    private suspend fun rate(user: User, review: String, rating: Int) {
        val result = withContext(Dispatchers.IO) { user.rate(mEbook, rating) }
        if (result != null) {
            setCurrentUser(user, xContext)
            logger.d(result.toString())
        }
        uploadReview(Review(user, mEbook, review, rating))
    }

    /**
     * Upload a review to Firebase. Note that an user can post at most one review per ebook, so only
     * the latest review is stored: the previous, if any, is overwritten.
     *
     * @param review instance to upload
     */
    private fun uploadReview(review: Review) {
        val reviews = mFirestore.collection("reviews")
        reviews.whereEqualTo("providerId", SimpleBiblioCommons.getProviderId(mEbook.providerName))
                .whereEqualTo("ebookId", mEbook.id)
                .whereEqualTo("reviewer", review.reviewer)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    val snapshots = task.result
                    if (task.isSuccessful && snapshots != null) {
                        var shouldAdd = true
                        snapshots.take(1).forEach {
                            logger.d(it.id + " => " + it.data)
                            updateReview(review, it.id)
                            shouldAdd = false
                        }
                        if (shouldAdd) reviews.add(review)
                                .addOnSuccessListener { documentReference: DocumentReference -> logger.d("DocumentSnapshot written with ID: " + documentReference.id) }
                                .addOnFailureListener { e: Exception? -> logger.w("Error adding document", e) }
                        updateUI(review)
                    } else logger.d("Error getting documents", task.exception)
                }
    }

    /**
     * Updates and existing review, overriding the rating value and the text.
     *
     * @param review the new object to insert.
     * @param id     identifier of document (i.e. the old review) to be updated.
     */
    private fun updateReview(review: Review, id: String) {
        val oldReview = mFirestore.collection("reviews").document(id)
        oldReview.update("rating", review.rating, "text", review.text)
                .addOnSuccessListener { logger.d("DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e: Exception? -> logger.w("Error updating document", e) }
    }

    /**
     * Retrieve from Firebase the reviews associated to current ebook and populates the view.
     */
    private fun retrieveReviews() {
        mFirestore.collection("reviews")
                .whereEqualTo("providerId", SimpleBiblioCommons.getProviderId(mEbook.providerName))
                .whereEqualTo("ebookId", mEbook.id)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    val snapshots = task.result
                    if (task.isSuccessful && snapshots != null) {
                        setupUI(snapshots.map {
                            it.toObject(Review::class.java)
                        }.toList())
                    } else
                        logger.d("Error getting documents", task.exception)
                }
    }

    /*
     * Hides the placeholders that are visible by default if no review is present. Call this if at
     * least one review has been retrieved.
     */
    private fun hidePlaceholders() {
        binding.reviewsIvTemplate.visibility = View.INVISIBLE
        binding.reviewsTvTemplate.visibility = View.INVISIBLE
    }

    /**
     * Shows the reviews passed in input.
     *
     * @param reviews the dataset to be displayed
     */
    private fun setupUI(reviews: List<Review>) {
        if (reviews.isEmpty()) {
            binding.reviewsIvTemplate.visibility = View.VISIBLE
            binding.reviewsTvTemplate.visibility = View.VISIBLE
        } else {
            hidePlaceholders()
            mAdapter.reviews = reviews
            mAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Updates the dataset inserting a new review.
     *
     * @param review the new inserted review
     */
    private fun updateUI(review: Review) {
        hidePlaceholders()
        mAdapter.addReview(review)
        mAdapter.notifyDataSetChanged()
    }
}