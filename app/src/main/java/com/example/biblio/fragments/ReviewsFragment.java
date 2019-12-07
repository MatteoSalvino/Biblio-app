package com.example.biblio.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.biblio.adapters.ReviewsAdapter;
import com.example.biblio.api.Review;
import com.example.biblio.api.User;
import com.example.biblio.api.UserBuilder;
import com.example.biblio.databinding.SwipeEbooksRvFragmentBinding;
import com.example.biblio.helpers.LogHelper;
import com.example.biblio.viewmodels.EbookDetailsViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

        EbookDetailsViewModel model = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(EbookDetailsViewModel.class);
        mEbook = model.getEbook().getValue();
        assert mEbook != null;

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.ebooksRv.setLayoutManager(mLayoutManager);
        binding.ebooksRv.setHasFixedSize(true);
        mAdapter = new ReviewsAdapter();
        binding.ebooksRv.setAdapter(mAdapter);
        //todo: replace with retrieveReviews
        loadFakeReviews();
        return binding.getRoot();
    }

    private void loadFakeReviews() {
        List<Review> mReviews = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User u = new UserBuilder().setUsername(String.format(Locale.getDefault(), "User - %d", i)).build();
            mReviews.add(new Review(u, mEbook, "Very nice ebook", 5 - i));
        }
        logger.d("adding fake reviews to populate the view");
        mAdapter.setReviews(mReviews);
        mAdapter.notifyDataSetChanged();
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
                        //todo: handle empty list of reviews
                        mAdapter.setReviews(mReviews);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        logger.d(String.format("Error getting documents: %s", task.getException()));
                    }
                });
    }
}
