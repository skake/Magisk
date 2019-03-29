package com.topjohnwu.magisk.model.flash

import android.net.Uri
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import com.topjohnwu.magisk.model.su.WriteOnlyList
import com.topjohnwu.magisk.model.zip.Zip
import com.topjohnwu.superuser.Shell
import java.io.File
import java.io.FileNotFoundException


open class ActionFlash : FlashManager() {

    lateinit var source: Uri

    private val tmpFile by lazy { File(context.cacheDir, "install.zip") }
    private val resolver get() = context.contentResolver
    private val out = WriteOnlyList { log(it) }

    @AnyThread
    override fun invoke() = checkSU()
        .map { source }
        .map {
            log("- Copying zip to temp directory")
            it.copyTo(tmpFile)
            log("- Copying zip successful")
            tmpFile
        }
        .map {
            log("- Unzipping binaries")
            it.unzip()
            tmpFile
        }
        .map {
            log("- Checking validity")
            if (!it.isMagiskModule()) {
                log("! This is not a Magisk Module!")
                throw IllegalArgumentException("Not a Magisk Module")
            }
            it
        }
        .map {
            log("- Installing from $it")
            it.install()
        }
        .map { it.toResult() }
        .doOnDispose {
            tmpFile.parentFile.listFiles().forEach { it.deleteRecursively() }
        }

    @WorkerThread
    private fun Uri.copyTo(file: File) = resolver
        .openInputStream(this)
        .orThrow(FileNotFoundException("$this cannot be found"))
        .copyTo(file.outputStream())

    @WorkerThread
    private fun File.unzip() = Zip {
        zip = this@unzip
        destination = parentFile
    }.unzip(
        "META-INF/com/google/android" to true
    )

    @WorkerThread
    private fun File.isMagiskModule(): Boolean {
        val updater = File(parentFile, "updater-script")
        return if (updater.exists()) {
            log("- Found updater-script, checking...")
            Shell.su("grep -q '#MAGISK' $updater").exec().isSuccess
        } else {
            log("! Cannot find updater-script")
            false
        }
    }

    @WorkerThread
    private fun File.install(): Boolean {
        val commands = arrayOf(
            "cd $parentFile",
            "BOOTMODE=true sh update-binary dummy 1 $this"
        )
        return Shell.su(*commands)
            .to(out, out)
            .exec().isSuccess
    }
}
