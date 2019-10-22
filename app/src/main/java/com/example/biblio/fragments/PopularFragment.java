package com.example.biblio.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.biblio.R;
import com.example.biblio.adapters.MyAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lrusso96.simplebiblio.core.Ebook;
import lrusso96.simplebiblio.core.SimpleBiblio;
import lrusso96.simplebiblio.core.SimpleBiblioBuilder;
import lrusso96.simplebiblio.core.providers.feedbooks.Feedbooks;
import lrusso96.simplebiblio.core.providers.feedbooks.FeedbooksBuilder;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesis;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesisBuilder;

public class PopularFragment extends Fragment implements MyAdapter.OnItemListener {
    private RecyclerView mPopularRecycleView;
    private MyAdapter.OnItemListener adapterListener;
    private ArrayList<Ebook> popularList;
    private SwipeRefreshLayout mSwipeContainer;
    private Boolean showDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popular_fragment, container, false);

        popularList = new ArrayList<>();

        mPopularRecycleView = v.findViewById(R.id.popular_rv);
        mPopularRecycleView.setHasFixedSize(true);

        mSwipeContainer = v.findViewById(R.id.swipePopularContainer);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mPopularRecycleView.setLayoutManager(mLayoutManager);


        //DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        //itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.item_decorator));
        //mPopularRecycleView.addItemDecoration(itemDecoration);

        adapterListener = this;
        showDialog = true;

        new loadPopular().execute();

        mSwipeContainer.setOnRefreshListener(() -> {
            showDialog = false;
            new loadPopular().execute();
            mSwipeContainer.setRefreshing(false);
        });

        mSwipeContainer.setColorSchemeResources(android.R.color.holo_orange_light);


        return v;
    }

    @Override
    public void onItemClick(int position) {
        Fragment to_render = new BookFragment();
        Bundle args = new Bundle();

        args.putString("current", new Gson().toJson(popularList.get(position)));
        to_render.setArguments(args);

        getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container)
                .getFragmentManager().beginTransaction().replace(R.id.fragment_container, to_render)
                .addToBackStack(null).commit();

    }

    private class loadPopular extends AsyncTask<Void, Void, List<Ebook>> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (showDialog) {
                progressDialog = ProgressDialog.show(getActivity(), "Loading",
                        "Loading. Please wait...", true);
                progressDialog.setContentView(R.layout.progress_dialog_view);
            }
        }

        @Override
        protected List<Ebook> doInBackground(Void... params) {
            Feedbooks feedbooks = new FeedbooksBuilder().build();
            LibraryGenesis libraryGenesis = new LibraryGenesisBuilder().build();
            SimpleBiblio biblio = new SimpleBiblioBuilder()
                    .addProvider(feedbooks)
                    .addProvider(libraryGenesis)
                    .build();

            List<Ebook> ret = biblio.getAllPopular();
            Log.d("PopularFragment", String.format("list's size : %d", ret.size()));
            return ret;
        }

        @Override
        protected void onPostExecute(List<Ebook> ebooks) {
            if (showDialog)
                progressDialog.dismiss();

            ArrayList<Ebook> res = new ArrayList<>(ebooks.size());
            res.addAll(ebooks);
            popularList = res;
            MyAdapter mAdapter = new MyAdapter(popularList, adapterListener, getContext());
            mPopularRecycleView.setAdapter(mAdapter);
        }
    }
}
