package com.example.biblio.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.biblio.BuildConfig;
import com.example.biblio.R;
import com.example.biblio.adapters.MyEbooksAdapter;
import com.example.biblio.databinding.MyEbooksFragmentBinding;
import com.example.biblio.helpers.LogHelper;
import com.example.biblio.helpers.SimpleBiblioHelper;
import com.example.biblio.viewmodels.EbookDetailsViewModel;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import lrusso96.simplebiblio.core.Ebook;

import static com.example.biblio.helpers.SDCardHelper.getFilename;

//todo: handle duplicates!
public class MyEbooksFragment extends Fragment implements MyEbooksAdapter.OnItemListener {
    public static final String TAG = "MyEBooksFragment";
    public final LogHelper logger = new LogHelper(getClass());
    private ArrayList<Ebook> mEbooks;
    private MyEbooksFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MyEbooksFragmentBinding.inflate(inflater, container, false);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.myEbooksRv.setLayoutManager(mLayoutManager);
        binding.myEbooksRv.setHasFixedSize(true);

        mEbooks = SimpleBiblioHelper.getMyEbooks(getContext());
        logger.d(mEbooks.toString());

        hideTemplates(mEbooks.isEmpty());
        MyEbooksAdapter.OnItemListener mMyEbooksListener = this;
        MyEbooksAdapter mAdapter = new MyEbooksAdapter(mEbooks, mMyEbooksListener, getContext());
        binding.myEbooksRv.setAdapter(mAdapter);

        return binding.getRoot();
    }

    /**
     * Launches an ebook reader to open the file.
     *
     * @param filename name of the file, already downloaded, with extension (e.g. file.txt)
     */
    private void openFile(@NotNull String filename) {
        File path = new File(String.format("%s/Biblio/%s", Environment.getExternalStorageDirectory(), filename));
        logger.d(String.format("open file:%s", path.toString()));

        Intent in = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getContext()),
                BuildConfig.APPLICATION_ID + ".provider",
                path);
        in.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        String extension = FilenameUtils.getExtension(filename);
        //todo: what about other extensions supported by Mu?
        if (extension.equals("epub"))
            in.setDataAndType(uri, "application/epub+zip");
        else if (extension.equals("pdf"))
            in.setDataAndType(uri, "application/pdf");
        startActivity(in);
    }

    @Override
    public void onReadButtonClick(int position) {
        Ebook current = mEbooks.get(position);
        openFile(getFilename(current));
    }

    private void hideTemplates(boolean should_hide) {
        int visibility = should_hide ? View.VISIBLE : View.INVISIBLE;
        binding.ivTemplate.setVisibility(visibility);
        binding.tvTemplate.setVisibility(visibility);
    }

    @Override
    public void onItemClick(int position) {
        Fragment to_render = new EbookDetailsFragment();
        EbookDetailsViewModel model = new ViewModelProvider(getActivity()).get(EbookDetailsViewModel.class);
        model.setEbook(mEbooks.get(position));
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, to_render)
                .addToBackStack(null).commit();
    }
}
