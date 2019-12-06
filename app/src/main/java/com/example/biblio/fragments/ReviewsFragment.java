package com.example.biblio.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.biblio.adapters.ReviewsAdapter;
import com.example.biblio.api.Review;
import com.example.biblio.databinding.SwipeEbooksRvFragmentBinding;
import com.example.biblio.helpers.LogHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lrusso96.simplebiblio.core.Ebook;

import static com.example.biblio.api.SimpleBiblioHelper.getProviderId;


public class ReviewsFragment extends Fragment {
    public static final String TAG = "ReviewsFragment";
    public final LogHelper logger = new LogHelper(getClass());
    private ReviewsAdapter mAdapter;
    private Ebook mEbook;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SwipeEbooksRvFragmentBinding binding = SwipeEbooksRvFragmentBinding.inflate(inflater, container, false);
        if (getArguments() != null)
            mEbook = new Gson().fromJson(getArguments().getString("current"), Ebook.class);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.ebooksRv.setLayoutManager(mLayoutManager);
        binding.ebooksRv.setHasFixedSize(true);
        mAdapter = new ReviewsAdapter();
        binding.ebooksRv.setAdapter(mAdapter);
        //todo: handle null ebook
        if (mEbook != null)
            retrieveReviews();
        return binding.getRoot();
    }

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
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            mReviews.add(rev);
                        }
                        mAdapter.setReviews(mReviews);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        logger.d(String.format("Error getting documents: %s", task.getException()));
                    }
                });
    }
}
