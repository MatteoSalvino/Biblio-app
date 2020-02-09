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
import com.example.biblio.api.RatingResult
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
import lrusso96.simplebiblio.core.Download
import lrusso96.simplebiblio.core.Ebook
import lrusso96.simplebiblio.core.Utils
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.util.*

class EbookDetailsFragment : XFragment(EbookDetailsFragment::class.java) {
    private lateinit var headerBinding: EbookDetailsFragmentHeaderBinding
    private lateinit var reviewsBinding: EbookDetailsFragmentReviewsBinding
    private lateinit var rootDir: File
    private var filename: String? = null
    private lateinit var current: Ebook
    private lateinit var downloadList: List<Download>
    private var currentStats: RatingResult? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = EbookDetailsFragmentBinding.inflate(inflater, container, false)
        val infosBinding = binding.infos
        val appbarBinding = binding.appbar
        headerBinding = binding.header
        reviewsBinding = binding.reviews
        val option = RequestOptions().centerInside()
        rootDir = File("${context?.getExternalFilesDir(null)?.absolutePath}/$APP_ROOT_DIR")
        val model = ViewModelProvider(activity!!).get(EbookDetailsViewModel::class.java)
        current = model.ebook.value!!
        logger.d("got ebook whose title is: ${current.title}")

        val user = getCurrentUser(context!!)
        if (user != null) {
            Thread(Runnable {
                currentStats = user.getEbookStats(current)
                activity?.runOnUiThread {
                    setDownloadCounter(currentStats?.downloads ?: 0)
                    setReviewsCounter(currentStats?.ratings ?: 0)
                    reviewsBinding.avgRate.rating = currentStats?.ratingAvg?.toFloat() ?: 0.0F
                }
            }).start()
        }
        headerBinding.title.text = current.title
        headerBinding.author.text = current.author
        if (current.cover != null) Glide.with(context!!).load(current.cover.toString()).apply(option).into(headerBinding.cover) else {
            headerBinding.cover.visibility = View.GONE
        }
        val bookDate = current.published
        val formatter = DateTimeFormatter.ofPattern("LL - yyyy")
        infosBinding.date.text = if (bookDate == null) "-" else bookDate.format(formatter)
        if (current.pages > 0) infosBinding.pages.text = "${current.pages}"
        if (current.language != null) infosBinding.language.text = current.language
        if (current.filesize > 0) infosBinding.size.text = Utils.bytesToReadableSize(current.filesize)
        binding.mainBookProvider.text = "by ${current.providerName}"
        if (current.summary != null)
            binding.mainBookSummary.text = current.summary
        else
            binding.mainBookSummary.setText(R.string.no_description)
        headerBinding.downloadBtn.isEnabled = false
        headerBinding.downloadBtn.setBackgroundColor(ContextCompat.getColor(context!!, R.color.disabled_button))
        Thread(Runnable {
            downloadList = current.downloads
            if (downloadList.isNotEmpty()) {
                filename = getFilename(current)
                activity?.runOnUiThread {
                    headerBinding.downloadBtn.isEnabled = true
                    headerBinding.downloadBtn.setBackgroundColor(ContextCompat.getColor(context!!, R.color.add_button))
                    showRemoveButton(isFavorite(current, context!!))
                }
            }
        }).start()

        appbarBinding.backBtn.setOnClickListener { popBackStackImmediate() }
        headerBinding.downloadBtn.setOnClickListener {
            if (isSDCardPresent()) {
                val multiplePermissionListener: MultiplePermissionsListener = object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            val path = "${context!!.getExternalFilesDir(null)?.absolutePath}/$APP_ROOT_DIR/$filename"
                            downloadFile(downloadList[0].uri.toString(), path)
                            Thread(Runnable {
                                val result = user?.notifyDownload(current)
                                if (result != null) {
                                    setCurrentUser(user, context!!)
                                    logger.d(result.toString())
                                }
                            }).start()
                        } else logger.d("Permissions not granted.")
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {}
                }
                val dialogMultiplePermissionsListener: MultiplePermissionsListener = DialogOnAnyDeniedMultiplePermissionsListener.Builder
                        .withContext(context)
                        .withTitle(R.string.storage_permission_title)
                        .withMessage(R.string.storage_permission_msg)
                        .withButtonText(android.R.string.ok)
                        .withIcon(context!!.getDrawable(R.drawable.baseline_error_outline_24))
                        .build()
                val compositePermissionsListener: MultiplePermissionsListener = CompositeMultiplePermissionsListener(dialogMultiplePermissionsListener, multiplePermissionListener)
                Dexter.withActivity(activity)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(compositePermissionsListener).check()
            } else {
                val errorMsg = resources.getString(R.string.no_sd_card_msg)
                logger.d(errorMsg)
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
        }

        headerBinding.removeBtn.setOnClickListener {
            val fn = filename
            if (fn != null)
                removeFile(rootDir, fn)
            removeEbook(current, context!!)
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

    private fun downloadFile(uri: String, path: String) {
        logger.d(String.format("download uri: %s", uri))
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Downloading")
        progressDialog.setIcon(R.drawable.download)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setCancelable(false)
        progressDialog.show()
        FileDownloader.setup(Objects.requireNonNull(context))
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
                        addEbook(current, context!!)
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