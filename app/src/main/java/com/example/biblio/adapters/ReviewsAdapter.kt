package com.example.biblio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.biblio.R
import com.example.biblio.adapters.ReviewsAdapter.ReviewsViewHolder
import com.example.biblio.api.Review
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.util.*

/**
 * RecyclerView adapter for a list of Reviews.
 */
class ReviewsAdapter : RecyclerView.Adapter<ReviewsViewHolder>() {
    var reviews: List<Review> = ArrayList()

    /**
     * Add a review to current dataset. It enforces the constraint that at most 1 review is allowed
     * per user: so, in case of conflict, overrides the old value.
     *
     * @param review new object to add to the collection
     */
    fun addReview(review: Review) {
        val reviews = reviews.filter { it.reviewer != review.reviewer }
                .toMutableList()
        reviews.add(review)
        this.reviews = reviews
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_review, parent, false)
        return ReviewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        val review = reviews[position]
        holder.username.text = review.reviewer
        holder.text.text = review.text
        holder.rating.rating = review.rating.toFloat()
    }

    override fun getItemCount() = reviews.size


    class ReviewsViewHolder internal constructor(view: View?) : RecyclerView.ViewHolder(view!!) {
        val username: TextView = itemView.findViewById(R.id.reviewItemUsername)
        val rating: MaterialRatingBar = itemView.findViewById(R.id.reviewItemRating)
        val text: TextView = itemView.findViewById(R.id.reviewItemText)
    }
}