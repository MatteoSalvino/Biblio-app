package com.example.biblio.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.biblio.R;
import com.example.biblio.api.RatingResult;
import com.example.biblio.api.User;
import com.example.biblio.databinding.EbookDetailsFragmentAppbarBinding;
import com.example.biblio.databinding.EbookDetailsFragmentBinding;
import com.example.biblio.databinding.EbookDetailsFragmentHeaderBinding;
import com.example.biblio.databinding.EbookDetailsFragmentInfosBinding;
import com.example.biblio.helpers.LogHelper;
import com.example.biblio.helpers.SDCardHelper;
import com.example.biblio.helpers.SimpleBiblioHelper;
import com.example.biblio.viewmodels.EbookDetailsViewModel;
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
import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import lrusso96.simplebiblio.core.Download;
import lrusso96.simplebiblio.core.Ebook;

import static com.example.biblio.helpers.SDCardHelper.APP_ROOT_DIR;
import static com.example.biblio.helpers.SDCardHelper.getFilename;
import static lrusso96.simplebiblio.core.Utils.bytesToReadableSize;

public class EbookDetailsFragment extends Fragment {
    private final LogHelper logger = new LogHelper(getClass());
    private EbookDetailsFragmentBinding binding;
    private File root_dir;
    private String filename;
    private Ebook current;
    private List<Download> downloadList;
    private RatingResult current_stats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EbookDetailsFragmentBinding.inflate(inflater, container, false);
        EbookDetailsFragmentInfosBinding infosBinding = binding.infos;
        EbookDetailsFragmentAppbarBinding appbarBinding = binding.appbar;
        EbookDetailsFragmentHeaderBinding headerBinding = binding.header;
        RequestOptions option = new RequestOptions().centerInside();
        root_dir = new File(String.format("%s/%s/", Environment.getExternalStorageDirectory(), APP_ROOT_DIR));

        EbookDetailsViewModel model = new ViewModelProvider(getActivity()).get(EbookDetailsViewModel.class);
        current = model.getEbook().getValue();
        assert current != null;
        logger.d(String.format("got ebook: %s", current.getTitle()));

        User user = SimpleBiblioHelper.getCurrentUser(getContext());

        if (user != null) {
            new Thread(() -> {
                current_stats = user.getEbookStats(current);
                if (current_stats == null) return;
                getActivity().runOnUiThread(() -> {
                    appbarBinding.mainBookAverageRate.setText(String.valueOf(current_stats.getRatingAvg()));
                    appbarBinding.mainBookReviewsCounter.setText(String.format(Locale.getDefault(), "%d %s", current_stats.getRatings(), (current_stats.getRatings() == 1) ? getResources().getString(R.string.review_template) : getResources().getString(R.string.reviews_template)));
                });
            }).start();
        }

        headerBinding.mainBookTitle.setText(current.getTitle());
        headerBinding.mainBookAuthor.setText(current.getAuthor());

        if (current.getCover() == null)
            Glide.with(Objects.requireNonNull(getContext())).load(R.drawable.no_image).into(headerBinding.mainBookCover);
        else {
            Glide.with(Objects.requireNonNull(getContext())).load(current.getCover().toString()).placeholder(R.drawable.no_image).apply(option).into(headerBinding.mainBookCover);
        }

        LocalDate book_date = current.getPublished();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        infosBinding.mainBookDate.setText((book_date == null) ? "-" : book_date.format(formatter));
        if (current.getPages() > 0)
            infosBinding.mainBookPages.setText(String.format("%s", current.getPages()));
        if (current.getLanguage() != null)
            infosBinding.mainBookLanguage.setText(current.getLanguage());
        if (current.getFilesize() > 0)
            infosBinding.mainBookSize.setText(bytesToReadableSize(current.getFilesize()));
        binding.mainBookProvider.setText(String.format("by %s", current.getProviderName()));

        if (current.getSummary() != null)
            binding.mainBookSummary.setText(current.getSummary());
        else
            binding.mainBookSummary.setText(R.string.no_description);

        binding.mainDownloadBtn.setEnabled(false);
        binding.mainDownloadBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));

        new Thread(() -> {
            downloadList = current.getDownloads();
            if (!downloadList.isEmpty()) {
                filename = getFilename(current);
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    binding.mainDownloadBtn.setEnabled(true);
                    showRemoveButton(SimpleBiblioHelper.isFavorite(current, getContext()));
                });
            }
        }).start();

        appbarBinding.mainBackBtn.setOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStackImmediate());

        binding.mainDownloadBtn.setOnClickListener(view -> {
            if (SDCardHelper.isSDCardPresent()) {
                MultiplePermissionsListener multiplePermissionListener = new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            String path = String.format("%s/%s/%s", Environment.getExternalStorageDirectory(), APP_ROOT_DIR, filename);
                            downloadFile(downloadList.get(0).getUri().toString(), path);

                            User user = SimpleBiblioHelper.getCurrentUser(getContext());
                            if (user != null) {
                                new Thread(() -> {
                                    RatingResult result = user.notifyDownload(current);
                                    if (result != null) {
                                        logger.d(result.toString());
                                    }
                                }).start();
                            }
                        } else
                            logger.d("Permissions not granted.");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                };

                //fixme: extract strings
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
                logger.d("SD Card not available");
                //fixme: extract string
                Toast.makeText(getContext(), "SD Card not found", Toast.LENGTH_LONG).show();
            }
        });

        binding.mainRemoveBtn.setOnClickListener(view -> {
            SDCardHelper.findFile(root_dir, filename, true);
            SimpleBiblioHelper.removeEbook(current, getContext());

            showRemoveButton(false);
        });

        //Check if selected book has already been downloaded
        if (root_dir.exists() && filename != null) {
            boolean present = SDCardHelper.findFile(root_dir, filename, false);
            showRemoveButton(present);
        }

        appbarBinding.mainReviewsBtn.setOnClickListener(view -> {
            Fragment to_render = new ReviewsFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, to_render)
                    .addToBackStack(null).commit();
        });

        return binding.getRoot();
    }

    private void downloadFile(String uri, String path) {
        logger.d(String.format("download uri: %s", uri));
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
                        logger.d("downloadFile - pending state");
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
                        SimpleBiblioHelper.addEbook(current, getContext());
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        logger.d("downloadFile - pause state");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        logger.e(e.getMessage());
                        progressDialog.dismiss();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        logger.w("downloadFile - warning state");
                    }
                }).start();
    }

    private void showRemoveButton(boolean bool) {
        if (bool) {
            binding.mainDownloadBtn.setVisibility(View.INVISIBLE);
            binding.mainRemoveBtn.setVisibility(View.VISIBLE);
        } else {
            binding.mainDownloadBtn.setVisibility(View.VISIBLE);
            binding.mainRemoveBtn.setVisibility(View.INVISIBLE);
        }
    }

    /* This shows how to retrieve ebook stats!
     * Note: use runonuithread() to update UI
     */
    private void showRating() {
        User user = SimpleBiblioHelper.getCurrentUser(getContext());
        if (user == null)
            return;
        new Thread(() -> {
            RatingResult ebookStats = user.getEbookStats(current);
            if (ebookStats != null) {
                logger.d(String.format(Locale.getDefault(), "%d reviews with average of %.1f", ebookStats.getRatings(), ebookStats.getRatingAvg()));
            }
        }).start();
    }
}
