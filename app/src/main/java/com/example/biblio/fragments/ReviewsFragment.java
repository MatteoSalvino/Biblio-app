package com.example.biblio.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.biblio.R;
import com.example.biblio.adapters.ReviewsAdapter;
import com.example.biblio.api.RatingResult;
import com.example.biblio.api.Review;
import com.example.biblio.api.User;
import com.example.biblio.api.UserBuilder;
import com.example.biblio.databinding.ReviewsFragmentAppbarBinding;
import com.example.biblio.databinding.ReviewsFragmentBinding;
import com.example.biblio.helpers.LogHelper;
import com.example.biblio.helpers.SimpleBiblioHelper;
import com.example.biblio.viewmodels.EbookDetailsViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lrusso96.simplebiblio.core.Ebook;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.example.biblio.api.SimpleBiblioCommons.getProviderId;

public class ReviewsFragment extends Fragment {
    public static final String TAG = "ReviewsFragment";
    public final LogHelper logger = new LogHelper(getClass());
    private ReviewsFragmentBinding binding;
    private ReviewsAdapter mAdapter;
    private Ebook mEbook;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ReviewsFragmentBinding.inflate(inflater, container, false);
        ReviewsFragmentAppbarBinding appbarBinding = binding.appbar;

        EbookDetailsViewModel model = new ViewModelProvider(getActivity()).get(EbookDetailsViewModel.class);
        mEbook = model.getEbook().getValue();
        assert mEbook != null;

        user = SimpleBiblioHelper.getCurrentUser(getContext());

        // Show reviews button to logged-in users only
        int visibility = user == null ? View.INVISIBLE : View.VISIBLE;
        appbarBinding.reviewsAddBtn.setVisibility(visibility);

        //Initialize AlertDialog to post a new review
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View dialogView = inflater.inflate(R.layout.review_dialog, null);
        builder.setView(dialogView);

        EditText reviewBody = dialogView.findViewById(R.id.review_body);
        MaterialRatingBar ratingBar = dialogView.findViewById(R.id.review_rating);
        MaterialButton cancelBtn = dialogView.findViewById(R.id.review_cancel_btn);
        MaterialButton postBtn = dialogView.findViewById(R.id.review_post_btn);
        postBtn.setEnabled(false);
        AlertDialog alertDialog = builder.create();

        reviewBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() >= 10 && ratingBar.getRating() > 0)
                    postBtn.setEnabled(true);
                else
                    postBtn.setEnabled(false);
            }
        });

        ratingBar.setOnRatingChangeListener((bar, rating) -> {
            if (bar.getRating() > 0 && reviewBody.getText().toString().length() >= 10)
                postBtn.setEnabled(true);
            else
                postBtn.setEnabled(false);
        });

        cancelBtn.setOnClickListener(myView -> alertDialog.dismiss());

        postBtn.setOnClickListener(myView -> {
            Toast.makeText(getContext(), reviewBody.getText().toString() + " " + ratingBar.getRating(), Toast.LENGTH_SHORT).show();

            new Thread(() -> {
                int rating = (int) ratingBar.getRating();
                RatingResult result = user.rate(mEbook, rating);
                if (result != null) {
                    logger.d(result.toString());
                }
                Review review = new Review(user, mEbook, reviewBody.getText().toString(), rating);
                uploadReview(review);
            }).start();

            alertDialog.dismiss();
            //Clean dialog's fields
            reviewBody.setText("");
            ratingBar.setRating(0);

        });

        appbarBinding.backBtn.setOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStackImmediate());
        appbarBinding.reviewsAddBtn.setOnClickListener(view -> alertDialog.show());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.ebooksRv.setLayoutManager(mLayoutManager);
        binding.ebooksRv.setHasFixedSize(true);
        mAdapter = new ReviewsAdapter();
        binding.ebooksRv.setAdapter(mAdapter);
        retrieveReviews();
        return binding.getRoot();
    }

    /**
     * This is just a debug method to (locally) generate fake reviews, without hitting the server.
     *
     * @param num: number of feke reviews to generate
     */
    private void loadFakeReviews(int num) {
        List<Review> mReviews = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            User u = new UserBuilder().setUsername(String.format(Locale.getDefault(), "User - %d", i)).build();
            mReviews.add(new Review(u, mEbook, "Very nice ebook", 5 - i));
        }
        logger.d("adding fake reviews to populate the view");
        mAdapter.setReviews(mReviews);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Upload a review to Firebase. Note that an user can post at most one review per book, so only
     * the latest review is stored: the previous, if any, is overwritten.
     *
     * @param review instance to upload
     */
    //TODO: implement method
    private void uploadReview(Review review) {
    }

    /**
     * Retrieve reviews from Firebase.
     */
    private void retrieveReviews() {
        List<Review> mReviews = new ArrayList<>();
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("reviews")
                .whereEqualTo("provider_id", getProviderId(mEbook.getProviderName()))
                .whereEqualTo("ebook_id", mEbook.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshots = task.getResult();
                        if (snapshots == null) {
                            logger.d("Null snapshot");
                            return;
                        }
                        for (QueryDocumentSnapshot document : snapshots) {
                            Review rev = document.toObject(Review.class);
                            logger.d(document.getId() + " => " + document.getData());
                            mReviews.add(rev);
                        }
                        if (mReviews.isEmpty()) {
                            binding.reviewsIvTemplate.setVisibility(View.VISIBLE);
                            binding.reviewsTvTemplate.setVisibility(View.VISIBLE);
                        } else {
                            binding.reviewsIvTemplate.setVisibility(View.INVISIBLE);
                            binding.reviewsTvTemplate.setVisibility(View.INVISIBLE);
                        }

                        mAdapter.setReviews(mReviews);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        logger.d(String.format("Error getting documents: %s", task.getException()));
                    }
                });
    }
}
