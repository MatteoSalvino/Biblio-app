package com.example.biblio.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;
import com.example.biblio.adapters.MyAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lrusso96.simplebiblio.core.Ebook;
import lrusso96.simplebiblio.core.providers.feedbooks.Feedbooks;
import lrusso96.simplebiblio.core.providers.feedbooks.FeedbooksBuilder;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesis;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesisBuilder;
import lrusso96.simplebiblio.exceptions.BiblioException;

public class RecentFragment extends Fragment implements MyAdapter.OnItemListener {
    private RecyclerView mRecentRecycleView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private MyAdapter.OnItemListener adapterListener;
    private ArrayList<Ebook> recentList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recent_fragment, container, false);

        recentList = new ArrayList<>();

        mRecentRecycleView = v.findViewById(R.id.recent_rv);
        mRecentRecycleView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecentRecycleView.setLayoutManager(mLayoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.item_decorator));
        mRecentRecycleView.addItemDecoration(itemDecoration);

        adapterListener = this;

        new loadRecent().execute();

        return v;
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


    private class loadRecent extends AsyncTask<Void, Void, List<Ebook>> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "Loading",
                    "Loading. Please wait...", true);
        }

        @Override
        protected List<Ebook> doInBackground(Void... voids) {
            List<Ebook> feedbooks_list = new ArrayList<>();
            List<Ebook> libgen_list = new ArrayList<>();

            Feedbooks feedbooks = new FeedbooksBuilder().build();
            LibraryGenesis libraryGenesis = new LibraryGenesisBuilder().build();

            try {
                feedbooks_list = feedbooks.getRecent();
                libgen_list = libraryGenesis.getRecent();
            } catch (BiblioException e) {
                e.printStackTrace();
            }

            feedbooks_list.addAll(libgen_list);

            return feedbooks_list;
        }

        @Override
        protected void onPostExecute(List<Ebook> ebooks) {
            progressDialog.dismiss();
            ArrayList<Ebook> res = new ArrayList<>(ebooks.size());
            res.addAll(ebooks);
            recentList = res;
            mAdapter = new MyAdapter(recentList,  adapterListener, getContext());
            mRecentRecycleView.setAdapter(mAdapter);
        }
    }
}
