package com.example.biblio.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblio.BuildConfig
import com.example.biblio.adapters.MyEbooksAdapter
import com.example.biblio.databinding.FragmentMyEbooksBinding
import com.example.biblio.helpers.SDCardHelper
import com.example.biblio.helpers.SDCardHelper.getFilename
import com.example.biblio.helpers.SimpleBiblioHelper.getMyEbooks
import com.example.biblio.helpers.XFragment
import com.example.biblio.viewmodels.EbookDetailsViewModel
import lrusso96.simplebiblio.core.Ebook
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.util.*

class MyEbooksFragment : XFragment(MyEbooksFragment::class.java), MyEbooksAdapter.OnItemListener {
    private lateinit var mEbooks: ArrayList<Ebook>
    private lateinit var binding: FragmentMyEbooksBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMyEbooksBinding.inflate(inflater, container, false)
        val mLayoutManager = LinearLayoutManager(xContext)
        binding.myEbooksRv.layoutManager = mLayoutManager
        binding.myEbooksRv.setHasFixedSize(true)
        mEbooks = getMyEbooks(xContext)
        logger.d(mEbooks.toString())
        hideTemplates(mEbooks.isEmpty())
        val mAdapter = MyEbooksAdapter(mEbooks, this, xContext)
        binding.myEbooksRv.adapter = mAdapter
        return binding.root
    }

    /**
     * Launches an ebook reader to open the file.
     *
     * @param filename name of the file, already downloaded, with extension (e.g. file.txt)
     */
    private fun openFile(filename: String) {
        val path = File("${xContext.getExternalFilesDir(null)?.absolutePath}/${SDCardHelper.APP_ROOT_DIR}/$filename")
        logger.d("open file: $path")
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(xContext,
                BuildConfig.APPLICATION_ID + ".provider",
                path)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //todo: what about other extensions supported by Mu?
        when (FilenameUtils.getExtension(filename)) {
            "epub" -> intent.setDataAndType(uri, "application/epub+zip")
            "pdf" -> intent.setDataAndType(uri, "application/pdf")
        }
        startActivity(intent)
    }

    override fun onReadButtonClick(position: Int) {
        val current = mEbooks[position]
        openFile(getFilename(current))
    }

    private fun hideTemplates(should_hide: Boolean) {
        val visibility = if (should_hide) View.VISIBLE else View.INVISIBLE
        binding.ivTemplate.visibility = visibility
        binding.tvTemplate.visibility = visibility
    }

    override fun onItemClick(position: Int) {
        val model = ViewModelProvider(activity!!).get(EbookDetailsViewModel::class.java)
        model.updateEbook(mEbooks[position])
        moveTo(EbookDetailsFragment())
    }
}