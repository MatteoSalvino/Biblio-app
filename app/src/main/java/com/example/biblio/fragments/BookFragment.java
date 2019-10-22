package com.example.biblio.fragments;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.biblio.helpers.CheckForSDCardHelper;
import com.example.biblio.R;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lrusso96.simplebiblio.core.Download;
import lrusso96.simplebiblio.core.Ebook;
import pub.devrel.easypermissions.EasyPermissions;

public class BookFragment extends Fragment {
    private TextView mBookTitle;
    private TextView mBookAuthor;
    private ImageView mBookCover;
    private TextView mBookDate;
    private TextView mBookPages;
    private TextView mBookSummary;
    private ImageView mBackBtn;
    private MaterialButton mDownloadBtn;
    private MaterialButton mRemoveBtn;
    private static final int WRITE_REQUEST_CODE = 300;
    private File root_dir;
    private String filename;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RequestOptions option;
    private Ebook current;
    private List<Download> downloadList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_fragment, container, false);
        option = new RequestOptions().centerCrop();

        mBookTitle = view.findViewById(R.id.main_book_title);
        mBookAuthor = view.findViewById(R.id.main_book_author);
        mBookCover = view.findViewById(R.id.main_book_cover);
        mBookDate = view.findViewById(R.id.main_book_date);
        mBookPages = view.findViewById(R.id.main_book_pages);
        mBookSummary = view.findViewById(R.id.main_book_summary);
        mBackBtn = view.findViewById(R.id.main_back_btn);
        mDownloadBtn = view.findViewById(R.id.main_download_btn);
        mRemoveBtn = view.findViewById(R.id.main_remove_btn);


        current = new Gson().fromJson(getArguments().getString("current"), new TypeToken<Ebook> () {}.getType());
        //Log.d("fromJson", current.getTitle() + ", " + current.getAuthor() + ", " + current.getPublished() + ", " + current.getPages() + ", " + current.getDownload().get(0).getExtension());

        //search_data = new Gson().fromJson(getArguments().getString("search_data"), new TypeToken<ArrayList<Ebook>> () {}.getType());

        //Show retrieved informations
        LocalDate book_date = current.getPublished();
        Integer book_pages = current.getPages();
        String book_summary = current.getSummary();

        mBookTitle.setText(current.getTitle());
        mBookAuthor.setText(current.getAuthor());

        if(current.getCover() == null)
            Glide.with(getContext()).load(R.drawable.no_image).into(mBookCover);
        else
            Glide.with(getContext()).load(current.getCover().toString()).placeholder(R.drawable.no_image).apply(option).into(mBookCover);

        mBookDate.setText((book_date == null) ? "No date available" : book_date.toString());
        mBookPages.setText("n° pages : " + ((book_pages == 0) ? "-" : String.valueOf(book_pages)));
        mBookSummary.setMovementMethod(new ScrollingMovementMethod());
        mBookSummary.setText((book_summary == null) ? "No summary available." : book_summary);



        root_dir = new File(Environment.getExternalStorageDirectory() + File.separator + "biblioData/");

        downloadList = current.getDownload();


        if(!current.getDownload().isEmpty())
            filename = mBookTitle.getText().toString()+"_"+mBookAuthor.getText().toString()+"_"+ mBookDate.getText().toString() + "." + downloadList.get(0).getExtension();
        else
            mDownloadBtn.setEnabled(false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = sharedPreferences.edit();


        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });


        Log.d("fileSource", ((current.getSource() == null) ? "null" : current.getSource()));
        new getDownloadUrl().execute();

        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckForSDCardHelper.isSDCardPresent()) {

                    MultiplePermissionsListener multiplePermissionListener = new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if(report.areAllPermissionsGranted())
                                downloadFile(downloadList.get(0).getUri().toString(), Environment.getExternalStorageDirectory() + "/biblioData/" + filename);
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
            }
        });

        mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckForSDCardHelper.findFile(root_dir, filename, true);
                String response = sharedPreferences.getString("mybooks", null);

                if(response != null) {
                    ArrayList<Ebook> myBooks = new Gson().fromJson(response, new TypeToken<ArrayList<Ebook>>() {}.getType());
                    if(!myBooks.isEmpty()) {
                        //Remove current book from array myBooks
                        int size = myBooks.size();

                        for(int i = 0; i < size; i++){
                            Ebook elem = myBooks.get(i);

                            if(elem.getId() == current.getId()){
                                myBooks.remove(i);
                                break;
                            }
                        }

                        editor.putString("mybooks", new Gson().toJson(myBooks));
                        editor.commit();
                    }
                }

                mRemoveBtn.setVisibility(View.INVISIBLE);
                mDownloadBtn.setVisibility(View.VISIBLE);

            }
        });


        //Check if selected book is yet downloaded
        if(root_dir.exists() && filename != null) {
            if (CheckForSDCardHelper.findFile(root_dir, filename, false)) {
                mDownloadBtn.setVisibility(View.INVISIBLE);
                mRemoveBtn.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }


    private class getDownloadUrl extends AsyncTask<Void, Ebook, URI> {


        @Override
        protected URI doInBackground(Void... voids) {
            URI download_url = null;
            Log.d("doInBackground", current.getProvider().getName());

            download_url = (downloadList.isEmpty()) ? null : downloadList.get(0).getUri();

            return download_url;
        }

        @Override
        protected void onPostExecute(URI uri) {
            if(uri == null) {
                mDownloadBtn.setEnabled(false);
                mDownloadBtn.setBackgroundColor(getResources().getColor(R.color.disableBtnColor));
                Log.d("DownloadTask", "null");
            } else {
                Log.d("DownloadTask", uri.toString());
            }

        }
    }

    private void downloadFile(String url, String path) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Downloading");
        progressDialog.setIcon(R.drawable.download);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.show();

        FileDownloader.setup(getContext());
        FileDownloader.getImpl().create(url)
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
                        progressDialog.setProgress((int)((soFarBytes * 100) / totalBytes));
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        progressDialog.dismiss();

                        mDownloadBtn.setVisibility(View.INVISIBLE);
                        mRemoveBtn.setVisibility(View.VISIBLE);

                        //Array saved in sharedPrefs is empty
                        String response = sharedPreferences.getString("mybooks", null);

                        if(response == null) {
                            ArrayList<Ebook> myBooks = new ArrayList<Ebook>();
                            myBooks.add(current);
                            editor.putString("mybooks", new Gson().toJson(myBooks));
                            editor.commit();
                        } else {
                            ArrayList<Ebook>  myBooks = new Gson().fromJson(response, new TypeToken<ArrayList<Ebook>>() {}.getType());
                            myBooks.add(current);
                            editor.putString("mybooks", new Gson().toJson(myBooks));
                            editor.commit();
                        }

                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d("downloadFile", "pause state");

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.d("downloadFile", "error state");
                        e.printStackTrace();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.d("downloadFile", "warning state");

                    }
                }).start();
    }
}
