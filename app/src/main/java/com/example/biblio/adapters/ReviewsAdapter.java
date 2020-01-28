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

    public void setReviews(@NotNull List<Review> reviews) {
        this.mReviews = reviews;
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