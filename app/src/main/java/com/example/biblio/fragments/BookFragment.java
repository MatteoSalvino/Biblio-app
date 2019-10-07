package com.example.biblio.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.biblio.helpers.CheckForSDCardHelper;
import com.example.biblio.R;
import com.example.biblio.models.Book;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class BookFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    private TextView mBookTitle;
    private TextView mBookAuthor;
    private ImageView mBookCover;
    private TextView mBookYear;
    private TextView mBookCategories;
    private TextView mBookRating;
    private TextView mBookDescription;
    private ImageView mBackBtn;
    private MaterialButton mDownloadBtn;
    private MaterialButton mRemoveBtn;
    private String url;
    private static final int WRITE_REQUEST_CODE = 300;
    private File root_dir;
    private String filename;
    private ArrayList<Book> myBooks;
    private Book current;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_fragment, container, false);

        mBookTitle = view.findViewById(R.id.main_book_title);
        mBookAuthor = view.findViewById(R.id.main_book_author);
        mBookCover = view.findViewById(R.id.main_book_cover);
        mBookYear = view.findViewById(R.id.main_book_year);
        mBookCategories = view.findViewById(R.id.main_book_categories);
        mBookRating = view.findViewById(R.id.main_book_rating);
        mBookDescription = view.findViewById(R.id.main_book_desc);
        mBackBtn = view.findViewById(R.id.main_back_btn);
        mDownloadBtn = view.findViewById(R.id.main_download_btn);
        mRemoveBtn = view.findViewById(R.id.main_remove_btn);

        //Retrieve book's informations
        final String book_title = getArguments().getString("book_title");
        final String book_author = getArguments().getString("book_author");
        final String book_cover = getArguments().getString("book_cover");
        final Integer book_year = getArguments().getInt("book_year");
        final ArrayList<String> book_categories = getArguments().getStringArrayList("book_categories");
        final Double book_rating = getArguments().getDouble("book_rating");
        final String book_description = getArguments().getString("book_desc");

        //Show retrieved informations

        mBookTitle.setText(book_title);
        mBookAuthor.setText(book_author);
        mBookCover.setImageResource(R.drawable.no_image);
        //mBookCover.setImageURI(Uri.parse(book_cover));
        mBookYear.setText(String.valueOf(book_year));

        String build_str = "";
        int bc_size = book_categories.size();
        for(int i = 0; i < bc_size; i++) {
            build_str.concat(book_categories.get(i));

            if(i < bc_size - 1)
                build_str.concat(", ");
            else
                build_str.concat("");
        }

        mBookCategories.setText(build_str);
        mBookRating.setText(String.valueOf(book_rating));
        mBookDescription.setText(book_description);

        current = new Book(book_title, book_author, book_cover, book_year, book_categories, book_rating, book_description);
        root_dir = new File(Environment.getExternalStorageDirectory() + File.separator + "biblioData/");
        filename = mBookTitle.getText().toString()+"_"+mBookAuthor.getText().toString()+"_"+mBookYear.getText().toString() +".epub";
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = sharedPreferences.edit();


        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckForSDCardHelper.isSDCardPresent()) {
                    if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Log.d("Permissions", "Permissions available");
                        url = "http://www.feedbooks.com/book/206.epub";
                        new DownloadFile().execute(url, filename);
                    } else {
                        Log.d("Permissions", "Permissions not available");
                        EasyPermissions.requestPermissions(getContext(), getString(R.string.write_file), WRITE_REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
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
                    ArrayList<Book> myBooks = new Gson().fromJson(response, new TypeToken<ArrayList<Book>>() {}.getType());
                    if(!myBooks.isEmpty()) {
                        //Remove current book from array myBooks
                        int size = myBooks.size();

                        for(int i = 0; i < size; i++){
                            Book elem = myBooks.get(i);

                            if(elem.getTitle().equals(book_title) && elem.getAuthor().equals(book_author) && elem.getCover_image().equals(book_cover)
                            && elem.getPublication_year() == book_year && elem.getRating().compareTo(book_rating) == 0 && elem.getCategories().equals(book_categories)
                            && elem.getDescription().equals(book_description)) {
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
        //Log.d("findFile", current_filename);
        if(root_dir.exists()) {
            if (CheckForSDCardHelper.findFile(root_dir, filename, false)) {
                mDownloadBtn.setVisibility(View.INVISIBLE);
                mRemoveBtn.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, getContext());
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        url = "http://www.feedbooks.com/book/206.epub";
        Log.d("onPermissionsGranted", "permissions granted");
        new DownloadFile().execute(url, filename);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d("No Permissions", "Permission has been denied");
    }

    private class DownloadFile extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        private String filename;
        private String folder;
        private boolean isDownloaded;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Downloading");
            progressDialog.setIcon(R.drawable.download);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            int count;
            InputStream input = null;
            OutputStream output = null;
            try {
                URL url = new URL(strings[0]);
                URLConnection connection = url.openConnection();
                connection.connect();


                int file_size = connection.getContentLength();

                Log.d("doInBackground", "file.length() : "+file_size);

                input = new BufferedInputStream(url.openStream(), 8192);
                //String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());

                filename = strings[1];
                //filename = timestamp + "_" + filename;

                Log.d("doInBackground", "filename : "+filename);

                folder = Environment.getExternalStorageDirectory() + File.separator + "biblioData/";
                File directory = new File(folder);

                Log.d("doInBackground", "folder : "+folder);

                if(!directory.exists()) {
                    Log.d("doInBackground", folder + " not exists");
                    directory.mkdirs();
                }

                output = new FileOutputStream(folder + filename);
                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1){
                    total += count;

                    publishProgress("" + (int)((total * 100) / file_size));
                    //Log.d("DialogProgress", "Progress: " + (int) ((total * 100) / file_size));

                    output.write(data, 0, count);
                }

                output.flush();

                isDownloaded = true;
                return "Downloaded at : " + folder + filename;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }finally {
                try {
                    input.close();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            isDownloaded = false;
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            this.progressDialog.dismiss();

            if(isDownloaded) {
                mDownloadBtn.setVisibility(View.INVISIBLE);
                mRemoveBtn.setVisibility(View.VISIBLE);
                //Array saved in sharedPrefs is empty
                String response = sharedPreferences.getString("mybooks", null);
                if(response == null) {
                    ArrayList<Book> myBooks = new ArrayList<Book>();
                    myBooks.add(current);
                    editor.putString("mybooks", new Gson().toJson(myBooks));
                    editor.commit();
                } else {
                   ArrayList<Book>  myBooks = new Gson().fromJson(response, new TypeToken<ArrayList<Book>>() {}.getType());
                   myBooks.add(current);
                   editor.putString("mybooks", new Gson().toJson(myBooks));
                   editor.commit();
                }
            }

            Toast.makeText(getContext(),
                    s, Toast.LENGTH_LONG).show();
        }
    }
}
