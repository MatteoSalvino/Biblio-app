package com.example.biblio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;
import com.example.biblio.api.Review;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a list of Reviews.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {
    private List<Review> mReviews = new ArrayList<>();

    public ReviewsAdapter() {
    }

    /**
     * Updates the current dataset.
     * Note that you should call notifyDatasetChanged after this, in order to update the view.
     *
     */
    public void setReviews(@NotNull List<Review> reviews) {
        this.mReviews = reviews;
    }


    /**
     * Add a review to current dataset. It enforces the constraint that at most 1 review is allowed
     * per user: so, in case of conflict, overrides the old value.
     *
     * @param review new object to add to the collection
     * @apiNote This method does not use Java 8 removeIf method, introduced in API 24, in order to
     * support API 21+.
     */
    public void addReview(Review review) {
        List<Review> reviews = new ArrayList<>();
        reviews.add(review);
        for (Review x : mReviews) {
            if (!x.getReviewer().equals(review.getReviewer()))
                reviews.add(x);
        }
        mReviews = reviews;
    }

    @NotNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.nameView.setText(review.getReviewer());
        holder.textView.setText(review.getText());
        holder.ratingBar.setRating((float) review.getRating());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public static class ReviewsViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        final MaterialRatingBar ratingBar;
        final TextView textView;

        ReviewsViewHolder(View v) {
            super(v);
            nameView = itemView.findViewById(R.id.reviewItemUsername);
            ratingBar = itemView.findViewById(R.id.reviewItemRating);
            textView = itemView.findViewById(R.id.reviewItemText);
        }
    }
}