package com.example.biblio.fragments

import android.Manifest
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biblio.R
import com.example.biblio.api.User
import com.example.biblio.databinding.EbookDetailsFragmentBinding
import com.example.biblio.databinding.EbookDetailsFragmentHeaderBinding
import com.example.biblio.databinding.EbookDetailsFragmentReviewsBinding
import com.example.biblio.helpers.SDCardHelper.APP_ROOT_DIR
import com.example.biblio.helpers.SDCardHelper.findFile
import com.example.biblio.helpers.SDCardHelper.getFilename
import com.example.biblio.helpers.SDCardHelper.isSDCardPresent
import com.example.biblio.helpers.SDCardHelper.removeFile
import com.example.biblio.helpers.SimpleBiblioHelper.addEbook
import com.example.biblio.helpers.SimpleBiblioHelper.getCurrentUser
import com.example.biblio.helpers.SimpleBiblioHelper.isFavorite
import com.example.biblio.helpers.SimpleBiblioHelper.removeEbook
import com.example.biblio.helpers.SimpleBiblioHelper.setCurrentUser
import com.example.biblio.helpers.XFragment
import com.example.biblio.viewmodels.EbookDetailsViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lrusso96.simplebiblio.core.Ebook
import lrusso96.simplebiblio.core.Utils
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

class EbookDetailsFragment : XFragment(EbookDetailsFragment::class.java) {
    private lateinit var ebook: Ebook
    private lateinit var headerBinding: EbookDetailsFragmentHeaderBinding
    private lateinit var reviewsBinding: EbookDetailsFragmentReviewsBinding
    private lateinit var rootDir: File
    private var filename: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = EbookDetailsFragmentBinding.inflate(inflater, container, false)
        val infosBinding = binding.infos
        val appbarBinding = binding.appbar
        headerBinding = binding.header
        reviewsBinding = binding.reviews
        rootDir = File("${xContext.getExternalFilesDir(null)?.absolutePath}/$APP_ROOT_DIR")
        val model = ViewModelProvider(activity!!).get(EbookDetailsViewModel::class.java)
        ebook = model.ebook.value!!
        logger.d("got ebook whose title is: ${ebook.title}")

        val user = getCurrentUser(xContext)
        if (user != null)
            uiScope.launch { retrieveStats(user) }
        headerBinding.title.text = ebook.title
        headerBinding.author.text = ebook.author
        if (ebook.cover != null)
            Glide.with(xContext).load(ebook.cover.toString()).apply(RequestOptions().centerInside()).into(headerBinding.cover)
        else headerBinding.cover.visibility = View.GONE

        val bookDate = ebook.published
        val formatter = DateTimeFormatter.ofPattern("LL - yyyy")
        infosBinding.date.text = if (bookDate == null) "-" else bookDate.format(formatter)
        if (ebook.pages > 0) infosBinding.pages.text = "${ebook.pages}"
        if (ebook.language != null) infosBinding.language.text = ebook.language
        if (ebook.filesize > 0) infosBinding.size.text = Utils.bytesToReadableSize(ebook.filesize)
        binding.mainBookProvider.text = "by ${ebook.providerName}"
        if (ebook.summary != null)
            binding.mainBookSummary.text = ebook.summary
        else
            binding.mainBookSummary.setText(R.string.no_description)
        headerBinding.downloadBtn.isEnabled = false
        headerBinding.downloadBtn.setBackgroundColor(ContextCompat.getColor(xContext, R.color.disabled_button))

        uiScope.launch { retrieveDownloads() }

        appbarBinding.backBtn.setOnClickListener { popBackStackImmediate() }
        headerBinding.downloadBtn.setOnClickListener {
            if (isSDCardPresent()) {
                val multiplePermissionListener: MultiplePermissionsListener = object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            val path = "${xContext.getExternalFilesDir(null)?.absolutePath}/$APP_ROOT_DIR/$filename"
                            downloadFile(ebook.downloads[0].uri.toString(), path)
                            Thread(Runnable {
                                val result = user?.notifyDownload(ebook)
                                if (result != null) {
                                    setCurrentUser(user, xContext)
                                    logger.d(result.toString())
                                }
                            }).start()
                        } else logger.d("Permissions not granted.")
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {}
                }
                val dialogMultiplePermissionsListener: MultiplePermissionsListener = DialogOnAnyDeniedMultiplePermissionsListener.Builder
                        .withContext(xContext)
                        .withTitle(R.string.storage_permission_title)
                        .withMessage(R.string.storage_permission_msg)
                        .withButtonText(android.R.string.ok)
                        .withIcon(xContext.getDrawable(R.drawable.baseline_error_outline_24))
                        .build()
                val compositePermissionsListener: MultiplePermissionsListener = CompositeMultiplePermissionsListener(dialogMultiplePermissionsListener, multiplePermissionListener)
                Dexter.withActivity(activity)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(compositePermissionsListener).check()
            } else {
                val errorMsg = resources.getString(R.string.no_sd_card_msg)
                logger.d(errorMsg)
                Toast.makeText(xContext, errorMsg, Toast.LENGTH_LONG).show()
            }
        }

        headerBinding.removeBtn.setOnClickListener {
            val fn = filename
            if (fn != null)
                removeFile(rootDir, fn)
            removeEbook(ebook, xContext)
            showRemoveButton(false)
        }

        //Check if selected book has already been downloaded
        val fn = filename
        if (rootDir.exists() && fn != null) {
            val present = findFile(rootDir, fn)
            showRemoveButton(present)
        }
        reviewsBinding.reviewsCounter.setOnClickListener { moveTo(ReviewsFragment()) }
        return binding.root
    }

    private suspend fun retrieveDownloads() {
        val downloads = withContext(Dispatchers.IO) { ebook.downloads }
        if (downloads.isNotEmpty()) {
            filename = getFilename(ebook)
            headerBinding.downloadBtn.isEnabled = true
            headerBinding.downloadBtn.setBackgroundColor(ContextCompat.getColor(xContext, R.color.add_button))
            showRemoveButton(isFavorite(ebook, xContext))
        }
    }

    private suspend fun retrieveStats(user: User) {
        val currentStats = withContext(Dispatchers.IO) { user.getEbookStats(ebook) }
        setDownloadCounter(currentStats?.downloads ?: 0)
        setReviewsCounter(currentStats?.ratings ?: 0)
        reviewsBinding.avgRate.rating = currentStats?.ratingAvg?.toFloat() ?: 0.0F
    }

    private fun downloadFile(uri: String, path: String) {
        logger.d(String.format("download uri: %s", uri))
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Downloading")
        progressDialog.setIcon(R.drawable.download)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setCancelable(false)
        progressDialog.show()
        FileDownloader.setup(xContext)
        FileDownloader.getImpl().create(uri)
                .setPath(path)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(object : FileDownloadListener() {
                    override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        logger.d("downloadFile - pending state")
                    }

                    override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        progressDialog.progress = soFarBytes * 100 / totalBytes
                    }

                    override fun completed(task: BaseDownloadTask) {
                        progressDialog.dismiss()
                        headerBinding.downloadBtn.visibility = View.INVISIBLE
                        headerBinding.removeBtn.visibility = View.VISIBLE
                        //todo: should open the new file?
                        addEbook(ebook, xContext)
                    }

                    override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        logger.d("downloadFile - pause state")
                    }

                    override fun error(task: BaseDownloadTask, e: Throwable) {
                        logger.e(e.message)
                        progressDialog.dismiss()
                    }

                    override fun warn(task: BaseDownloadTask) {
                        logger.w("downloadFile - warning state")
                    }
                }).start()
    }

    private fun showRemoveButton(bool: Boolean) {
        if (bool) {
            headerBinding.downloadBtn.visibility = View.INVISIBLE
            headerBinding.removeBtn.visibility = View.VISIBLE
        } else {
            headerBinding.downloadBtn.visibility = View.VISIBLE
            headerBinding.removeBtn.visibility = View.INVISIBLE
        }
    }

    private fun setDownloadCounter(downloads: Int) {
        var tv = resources.getString(R.string.downloads_template)
        if (downloads == 1) tv = resources.getString(R.string.download_template)
        reviewsBinding.downoadsCounter.text = String.format("%s %s", downloads, tv)
    }

    private fun setReviewsCounter(reviews: Int) {
        var tv = resources.getString(R.string.reviews_template)
        if (reviews == 1) tv = resources.getString(R.string.review_template)
        reviewsBinding.reviewsCounter.text = String.format("%s %s", reviews, tv)
    }
}