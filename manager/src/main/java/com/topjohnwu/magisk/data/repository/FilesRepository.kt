package com.topjohnwu.magisk.data.repository

import android.content.Context
import com.topjohnwu.magisk.Constants
import com.topjohnwu.magisk.data.network.GithubRawApiServices
import com.topjohnwu.magisk.util.withStreams
import io.reactivex.Single
import okhttp3.ResponseBody
import java.io.File


class FilesRepository(
    private val context: Context,
    private val rawApi: GithubRawApiServices
) {
    /*TODO replace url with a constant or something, this is just fkin stupid*/
    @Deprecated("")
    private fun fetchZip(url: String, fileName: String = "install.zip") =
        Single.error<File>(IllegalStateException())

    fun fetchBootCtl(fileName: String = "bootctl") =
        rawApi.fetchBootctl().map { it.writeToFile(fileName) }

    fun fetchMagisk(fileName: String = "magisk.zip") = fetchZip(Constants.Url.MAGISK_URL)

    private fun ResponseBody.writeToFile(fileName: String): File {
        val file = File(context.cacheDir, fileName)
        withStreams(byteStream(), file.outputStream()) { inStream, outStream ->
            inStream.copyTo(outStream)
        }
        return file
    }

}
