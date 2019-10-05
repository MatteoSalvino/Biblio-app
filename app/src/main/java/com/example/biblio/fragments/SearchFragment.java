package com.example.biblio.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;
import com.example.biblio.adapters.MyAdapter;
import com.example.biblio.models.Book;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements MyAdapter.OnItemListener {
    private RecyclerView mRecycleView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Book> myDataset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        final MaterialSearchBar mSearchBar = view.findViewById(R.id.searchBar);

        ArrayList<String> categories = new ArrayList<>();
        categories.add("Romance");
        categories.add("Fiction");
        myDataset = new ArrayList<>();
        myDataset.add(new Book("La divina commedia", "Dante Alighieri", "no url", 1990, categories,  4.3, "description not available"));
        myDataset.add(new Book("L'iliade", "Omero", "no url", 1880, categories, 4.5, "description not available"));

        mRecycleView = view.findViewById(R.id.recycler_view);
        mRecycleView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(myDataset, this);
        mRecycleView.setAdapter(mAdapter);

        mSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("onTextChanged", "Text changed in "+ String.valueOf(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("Text Changed", editable.toString());
                //search(editable.toString, website_flag);
            }
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Log.d("onItemClicked", myDataset.get(position).toString());

        Fragment to_render = new BookFragment();
        Bundle args = new Bundle();

        args.putString("book_title", myDataset.get(position).getTitle());
        args.putString("book_author", myDataset.get(position).getAuthor());
        args.putString("book_cover", myDataset.get(position).getCover_image());
        args.putInt("book_year", myDataset.get(position).getPublication_year());
        args.putStringArrayList("book_categories", myDataset.get(position).getCategories());
        args.putDouble("book_rating", myDataset.get(position).getRating());
        args.putString("book_desc", myDataset.get(position).getDescription());

        to_render.setArguments(args);

        getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container)
                .getFragmentManager().beginTransaction().replace(R.id.fragment_container, to_render)
                .addToBackStack(null).commit();
    }
}
