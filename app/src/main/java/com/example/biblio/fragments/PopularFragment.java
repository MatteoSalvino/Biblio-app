package com.example.biblio.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;
import com.example.biblio.adapters.MyAdapter;
import com.example.biblio.adapters.MyBooksAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lrusso96.simplebiblio.core.Ebook;
import lrusso96.simplebiblio.core.providers.feedbooks.Feedbooks;
import lrusso96.simplebiblio.core.providers.feedbooks.FeedbooksBuilder;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesis;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesisBuilder;
import lrusso96.simplebiblio.exceptions.BiblioException;

public class PopularFragment extends Fragment implements MyAdapter.OnItemListener {
    private RecyclerView mPopularRecycleView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private MyAdapter.OnItemListener adapterListener;
    private ArrayList<Ebook> popularList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popular_fragment, container, false);

        popularList = new ArrayList<>();

        mPopularRecycleView = v.findViewById(R.id.popular_rv);
        mPopularRecycleView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mPopularRecycleView.setLayoutManager(mLayoutManager);

        //DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        //itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.item_decorator));
        //mPopularRecycleView.addItemDecoration(itemDecoration);

        adapterListener = this;

        new loadPopular().execute();

        return  v;
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
            progressDialog = ProgressDialog.show(getActivity(), "Loading",
                    "Loading. Please wait...", true);
            progressDialog.setContentView(R.layout.progress_dialog_view);
        }

        @Override
        protected List<Ebook> doInBackground(Void... voids) {
            List<Ebook> feedbooks_list = new ArrayList<>();
            List<Ebook> libgen_list = new ArrayList<>();

            Feedbooks feedbooks = new FeedbooksBuilder().build();
            LibraryGenesis libraryGenesis = new LibraryGenesisBuilder().build();

            try {
                feedbooks_list = feedbooks.getPopular();
                libgen_list = libraryGenesis.getPopular();
            } catch (BiblioException e) {
                e.printStackTrace();
            }
            Log.d("PopularFragment", "feedboks_list's size : " + feedbooks_list.size() + ", libgen_list's size : " + libgen_list.size());

            feedbooks_list.addAll(libgen_list);

            return feedbooks_list;
        }

        @Override
        protected void onPostExecute(List<Ebook> ebooks) {
            progressDialog.dismiss();
            ArrayList<Ebook> res = new ArrayList<>(ebooks.size());
            res.addAll(ebooks);
            popularList = res;
            mAdapter = new MyAdapter(popularList,  adapterListener, getContext());
            mPopularRecycleView.setAdapter(mAdapter);
        }
    }
}
