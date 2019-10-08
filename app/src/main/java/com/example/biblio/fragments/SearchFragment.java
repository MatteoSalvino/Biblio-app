package com.example.biblio.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.jakewharton.rxbinding.widget.RxTextView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lrusso96.simplebiblio.core.Ebook;
import lrusso96.simplebiblio.core.SimpleBiblio;
import lrusso96.simplebiblio.core.SimpleBiblioBuilder;
import lrusso96.simplebiblio.exceptions.BiblioException;

public class SearchFragment extends Fragment implements MyAdapter.OnItemListener {
    private RecyclerView mRecycleView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Ebook> myDataset;
    private SimpleBiblio simpleBiblio;
    private MyAdapter.OnItemListener adapterListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        final MaterialSearchBar mSearchBar = view.findViewById(R.id.searchBar);

        myDataset = new ArrayList<>();
        simpleBiblio = new SimpleBiblioBuilder().build();

        mRecycleView = view.findViewById(R.id.recycler_view);
        mRecycleView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(mLayoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.item_decorator));
        mRecycleView.addItemDecoration(itemDecoration);

        adapterListener = this;

        RxTextView.textChanges(mSearchBar.getSearchEditText())
                .debounce(750, TimeUnit.MILLISECONDS)
                .subscribe(textChanged -> {
                    Log.d("TextChanges", "Stopped typing.");
                    String query = mSearchBar.getSearchEditText().getText().toString();

                    if (query.length() >= 5)
                        new SearchTask().execute(query);
                    else
                        Log.d("QueryAlert", "Query too short !");
                });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        //Log.d("onItemClicked", myDataset.get(position).toString());

        Fragment to_render = new BookFragment();
        Bundle args = new Bundle();

        args.putString("current", new Gson().toJson(myDataset.get(position)));

        to_render.setArguments(args);

        getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container)
                .getFragmentManager().beginTransaction().replace(R.id.fragment_container, to_render)
                .addToBackStack(null).commit();
    }

    public class SearchTask extends AsyncTask<String, Void, List<Ebook>> {

        @Override
        protected List<Ebook> doInBackground(String... params) {
            String query = params[0];
            List<Ebook> results = null;

            try {
                results = simpleBiblio.searchAll(query);
            } catch (BiblioException e) {
                e.printStackTrace();
            }

            if(results == null)
                Log.d("SearchStatus", "something goes wrong !");

            return results;
        }

        @Override
        protected void onPostExecute(List<Ebook> ebooks) {
            if (ebooks != null) {

                //Filter ebooks with unknown extension
                List<Ebook> filtered_results = new ArrayList<Ebook>();
                for (Ebook elem : ebooks){
                    if(elem.getExtension() != null)
                        filtered_results.add(elem);
                }

                myDataset = filtered_results;
                Log.d("result's size : ", String.valueOf(ebooks.size()));
                Log.d("filtered_result's size", String.valueOf(myDataset.size()));
                mAdapter = new MyAdapter(myDataset, adapterListener, getContext());
                mRecycleView.setAdapter(mAdapter);
            }
        }
    }
}
