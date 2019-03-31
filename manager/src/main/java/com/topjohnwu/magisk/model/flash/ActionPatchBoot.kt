package com.topjohnwu.magisk.model.flash

import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toFile
import com.topjohnwu.magisk.Config
import com.topjohnwu.magisk.Constants
import com.topjohnwu.magisk.util.*
import com.topjohnwu.superuser.Shell
import io.reactivex.Single
import org.kamranzafar.jtar.TarEntry
import org.kamranzafar.jtar.TarInputStream
import org.kamranzafar.jtar.TarOutputStream
import java.io.File


class ActionPatchBoot : MagiskInstaller() {
    override val boot: File by lazy { File(installDir, "boot.img") }
    override val installDir: File by lazy { File(context.filesDir.parent, "install") }

    lateinit var source: Uri

    override fun invoke(): Single<Result> = Single.just(source)
        .map { it.toFile().openStream(it).copyTo(boot) }
        .flatMap { construct() }
        .map {
            val bootFormat = Config.bootFormat
            val patchedBoot = File(Constants.EXTERNAL_PATH, "patched_boot.$bootFormat").apply {
                parentFile.mkdirs()
            }

            val writableStream = patchedBoot.openOutputStream(bootFormat) {
                putNextEntry(TarEntry(it, "boot.img"))
            }

            withStreams(it.suInputStream(), writableStream) { reader, writer ->
                reader.copyTo(writer)
            }

            log("")
            log("****************************")
            log(" Patched image is placed in ")
            log(" $patchedBoot ")
            log("****************************")

            Shell.sh("rm -f $it").exec().isSuccess.toResult()
        }

    private fun File.openOutputStream(format: String, creator: TarOutputStream.() -> Unit) =
        when (format) {
            Constants.IMG -> outputStream()
            Constants.IMG_TAR -> tarOutputStream().apply(creator)
            else -> throw IllegalArgumentException("Unknown format $format")
        }

    private fun File.openStream(uri: Uri) = if (uri.name?.endsWith(".tar") == true) {
        tarInputStream().apply { moveToBoot() }
    } else {
        inputStream()
    }

    @Throws(IllegalStateException::class)
    private fun TarInputStream.moveToBoot() {
        var entry: TarEntry? = nextEntry
        while (entry != null) {
            if (entry.name == "boot.img") return
            entry = nextEntry
        }
        throw IllegalStateException("Cannot find boot.img in given tar")
    }

    private val Uri.name
        get() = context.contentResolver.query(this, null, null, null, null).use {
            val name = it?.getColumnIndex(OpenableColumns.DISPLAY_NAME)?.let { index ->
                if (index != -1) {
                    it.moveToFirst()
                    it.getString(index)
                } else {
                    null
                }
            }

            if (name.isNullOrBlank()) {
                path?.let { path ->
                    val index = path.lastIndexOf('/')
                    path.substring(index + 1)
                }
            } else {
                name
            }
        }

}
