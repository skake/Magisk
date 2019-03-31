package com.topjohnwu.magisk.data.repository

import android.content.Context
import com.topjohnwu.magisk.Constants
import com.topjohnwu.magisk.data.network.ApiServices
import com.topjohnwu.magisk.util.withStreams
import java.io.File


class FilesRepository(
    private val api: ApiServices,
    private val context: Context
) {
    /*TODO replace url with a constant or something, this is just fkin stupid*/
    private fun fetchZip(url: String, fileName: String = "install.zip") = api.fetchZip(url)
        .map {
            val file = File(context.cacheDir, fileName)
            withStreams(it.byteStream(), file.outputStream()) { inStream, outStream ->
                inStream.copyTo(outStream)
            }
            file
        }

    fun fetchBootCtl(fileName: String = "bootctl") = fetchZip(Constants.Url.BOOTCTL_URL, fileName)
    fun fetchMagisk(fileName: String = "magisk.zip") = fetchZip(Constants.Url.MAGISK_URL)

}
