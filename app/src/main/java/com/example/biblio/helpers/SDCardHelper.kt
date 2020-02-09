package com.example.biblio.helpers

import android.os.Environment
import lrusso96.simplebiblio.core.Ebook
import java.io.File

object SDCardHelper {
    const val APP_ROOT_DIR = "Biblio"

    @JvmStatic
    fun isSDCardPresent() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    @JvmStatic
    fun findFile(dir: File, filename: String) = dir.listFiles()?.any { it.name == filename }
            ?: false

    @JvmStatic
    fun removeFile(dir: File, filename: String) = dir.listFiles()?.filter { it.name == filename }?.forEach { it.delete() }

    /**
     * @param ebook instance
     * @return its filename
     * @implNote Assume at least one available download
     */
    @JvmStatic
    fun getFilename(ebook: Ebook) = "${ebook.title}_${ebook.author}_${ebook.published}.${ebook.downloads[0].extension}"
}