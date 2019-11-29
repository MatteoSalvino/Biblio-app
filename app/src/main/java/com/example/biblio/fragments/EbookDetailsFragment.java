package com.example.biblio.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.biblio.R;
import com.example.biblio.databinding.EbookFragmentBinding;
import com.example.biblio.helpers.SDCardHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.threeten.bp.LocalDate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lrusso96.simplebiblio.core.Download;
import lrusso96.simplebiblio.core.Ebook;

import static com.example.biblio.helpers.SDCardHelper.APP_ROOT_DIR;
import static com.example.biblio.helpers.SharedPreferencesHelper.MY_EBOOKS_TAG;

//todo: add view model
public class EbookDetailsFragment extends Fragment {
    //fixme: variable not used
    private static final int WRITE_REQUEST_CODE = 300;
    private EbookFragmentBinding binding;
    private File root_dir;
    private String filename;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Ebook current;
    private List<Download> downloadList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EbookFragmentBinding.inflate(inflater, container, false);
        RequestOptions option = new RequestOptions().centerCrop();

        current = new Gson().fromJson(getArguments().getString("current"), new TypeToken<Ebook>() {
        }.getType());

        //search_data = new Gson().fromJson(getArguments().getString("search_data"), new TypeToken<ArrayList<Ebook>> () {}.getType());

        //Show retrieved informations
        LocalDate book_date = Objects.requireNonNull(current).getPublished();
        int book_pages = current.getPages();
        String book_summary = current.getSummary();

        binding.mainBookTitle.setText(current.getTitle());
        binding.mainBookAuthor.setText(current.getAuthor());

        if (current.getCover() == null)
            Glide.with(Objects.requireNonNull(getContext())).load(R.drawable.no_image).into(binding.mainBookCover);
        else
            Glide.with(Objects.requireNonNull(getContext())).load(current.getCover().toString()).placeholder(R.drawable.no_image).apply(option).into(binding.mainBookCover);

        binding.mainBookDate.setText((book_date == null) ? "No date available" : book_date.toString());
        binding.mainBookPages.setText(String.format("n° pages : %s", (book_pages == 0) ? "-" : String.valueOf(book_pages)));
        binding.mainBookSummary.setMovementMethod(new ScrollingMovementMethod());
        binding.mainBookSummary.setText((book_summary == null) ? "No summary available." : book_summary);

        root_dir = new File(String.format("%s/%s/", Environment.getExternalStorageDirectory(), APP_ROOT_DIR));

        binding.mainDownloadBtn.setEnabled(false);
        binding.mainDownloadBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));

        new Thread(() -> {
            downloadList = current.getDownloads();
            if (!downloadList.isEmpty()) {
                //todo: filename should be returned by some Helper Class
                filename = binding.mainBookTitle.getText().toString() + "_" + binding.mainBookAuthor.getText().toString() + "_" + binding.mainBookDate.getText().toString() + "." + downloadList.get(0).getExtension();
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> binding.mainDownloadBtn.setEnabled(true));
            }
        }).start();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = sharedPreferences.edit();

        binding.mainBackBtn.setOnClickListener(view13 -> Objects.requireNonNull(getFragmentManager()).popBackStackImmediate());
        Log.d("fileSource", ((current.getSource() == null) ? "null" : current.getSource()));

        binding.mainDownloadBtn.setOnClickListener(view1 -> {
            if (SDCardHelper.isSDCardPresent()) {
                MultiplePermissionsListener multiplePermissionListener = new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted())
                            downloadFile(downloadList.get(0).getUri().toString(), String.format("%s/%s/%s", Environment.getExternalStorageDirectory(), APP_ROOT_DIR, filename));
                        else
                            Log.d("Permissions", "Permissions not available.");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                };

                MultiplePermissionsListener dialogMultiplePermissionsListener =
                        DialogOnAnyDeniedMultiplePermissionsListener.Builder
                                .withContext(getContext())
                                .withTitle("Read/Write external local storage permission")
                                .withMessage("Both read and write permission are needed to store and retrieve downloaded files.")
                                .withButtonText(android.R.string.ok)
                                .withIcon(getContext().getDrawable(R.drawable.baseline_error_outline_24))
                                .build();

                MultiplePermissionsListener compositePermissionsListener = new CompositeMultiplePermissionsListener(dialogMultiplePermissionsListener, multiplePermissionListener);

                Dexter.withActivity(getActivity())
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(compositePermissionsListener).check();
            } else {
                Log.d("SD Card", "SD Card not available");
                Toast.makeText(getContext(), "SD Card not found", Toast.LENGTH_LONG).show();
            }
        });

        binding.mainRemoveBtn.setOnClickListener(view12 -> {
            SDCardHelper.findFile(root_dir, filename, true);
            String response = sharedPreferences.getString(MY_EBOOKS_TAG, null);

            if (response != null) {
                ArrayList<Ebook> myEbooks = new Gson().fromJson(response, new TypeToken<ArrayList<Ebook>>() {
                }.getType());
                if (!myEbooks.isEmpty()) {
                    //Remove current book from array myBooks
                    int size = myEbooks.size();

                    for (int i = 0; i < size; i++) {
                        Ebook elem = myEbooks.get(i);
                        if (elem.getId() == current.getId()) {
                            myEbooks.remove(i);
                            break;
                        }
                    }
                    editor.putString(MY_EBOOKS_TAG, new Gson().toJson(myEbooks));
                    editor.apply();
                }
            }
            binding.mainRemoveBtn.setVisibility(View.INVISIBLE);
            binding.mainDownloadBtn.setVisibility(View.VISIBLE);
        });

        //Check if selected book is yet downloaded
        if (root_dir.exists() && filename != null) {
            if (SDCardHelper.findFile(root_dir, filename, false)) {
                binding.mainDownloadBtn.setVisibility(View.INVISIBLE);
                binding.mainRemoveBtn.setVisibility(View.VISIBLE);
            }
        }
        return binding.getRoot();
    }

    //fixme: Libgen uris are not valid!
    private void downloadFile(String uri, String path) {
        Log.d("download uri", uri);
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Downloading");
        progressDialog.setIcon(R.drawable.download);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.show();

        FileDownloader.setup(Objects.requireNonNull(getContext()));
        FileDownloader.getImpl().create(uri)
                .setPath(path)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d("downloadFile", "pending state");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        progressDialog.setProgress(((soFarBytes * 100) / totalBytes));
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        progressDialog.dismiss();
                        binding.mainDownloadBtn.setVisibility(View.INVISIBLE);
                        binding.mainRemoveBtn.setVisibility(View.VISIBLE);

                        //todo: should open the new file?

                        //Array saved in sharedPrefs is empty
                        String response = sharedPreferences.getString(MY_EBOOKS_TAG, null);

                        ArrayList<Ebook> myEbooks;
                        if (response == null) {
                            myEbooks = new ArrayList<>();
                            myEbooks.add(current);
                            editor.putString(MY_EBOOKS_TAG, new Gson().toJson(myEbooks));
                            editor.commit();
                        } else {
                            myEbooks = new Gson().fromJson(response, new TypeToken<ArrayList<Ebook>>() {
                            }.getType());
                            myEbooks.add(current);
                            editor.putString(MY_EBOOKS_TAG, new Gson().toJson(myEbooks));
                            editor.commit();
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d("downloadFile", "pause state");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.d("downloadFile", "" + e.getMessage());
                        progressDialog.dismiss();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.d("downloadFile", "warning state");
                    }
                }).start();
    }
}
