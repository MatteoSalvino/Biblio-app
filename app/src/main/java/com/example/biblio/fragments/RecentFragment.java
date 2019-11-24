package com.example.biblio.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;
import com.example.biblio.adapters.MyAdapter;
import com.example.biblio.viewmodels.RecentViewModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lrusso96.simplebiblio.core.Ebook;

public class RecentFragment extends Fragment implements MyAdapter.OnItemListener {
    private RecyclerView mRecentRecycleView;
    private MyAdapter.OnItemListener adapterListener;
    private ArrayList<Ebook> recentList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recent_fragment, container, false);
        mRecentRecycleView = view.findViewById(R.id.recent_rv);
        mRecentRecycleView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecentRecycleView.setLayoutManager(mLayoutManager);

        adapterListener = this;
        RecentViewModel model = ViewModelProviders.of(getActivity()).get(RecentViewModel.class);

        final Observer<List<Ebook>> recentObserver = ebooks -> {
            ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading",
                    "Loading. Please wait...", true);
            progressDialog.setContentView(R.layout.progress_dialog_view);
            recentList = (ArrayList<Ebook>) ebooks;
            MyAdapter mAdapter = new MyAdapter(recentList, adapterListener, getContext());
            mRecentRecycleView.setAdapter(mAdapter);
            progressDialog.dismiss();
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        model.getEbooks().observe(this, recentObserver);
        return view;
    }

    @Override
    public void onItemClick(int position) {
        Fragment to_render = new BookFragment();
        Bundle args = new Bundle();

        args.putString("current", new Gson().toJson(recentList.get(position)));
        to_render.setArguments(args);

        getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container)
                .getFragmentManager().beginTransaction().replace(R.id.fragment_container, to_render)
                .addToBackStack(null).commit();
    }
}
