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
    private List<Review> reviews = new ArrayList<>();

    public ReviewsAdapter() {
    }

    /**
     * Updates the current dataset.
     * Note that you should call notifyDatasetChanged after this, in order to update the view.
     */
    public void setReviews(@NotNull List<Review> reviews) {
        this.reviews = reviews;
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
        List<Review> newReviews = new ArrayList<>();
        newReviews.add(review);
        for (Review rev : reviews) {
            if (!rev.getReviewer().equals(review.getReviewer()))
                newReviews.add(rev);
        }
        this.reviews = newReviews;
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
        Review review = reviews.get(position);
        holder.username.setText(review.getReviewer());
        holder.text.setText(review.getText());
        holder.rating.setRating((float) review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewsViewHolder extends RecyclerView.ViewHolder {
        final TextView username;
        final MaterialRatingBar rating;
        final TextView text;

        ReviewsViewHolder(View view) {
            super(view);
            username = itemView.findViewById(R.id.reviewItemUsername);
            rating = itemView.findViewById(R.id.reviewItemRating);
            text = itemView.findViewById(R.id.reviewItemText);
        }
    }
}