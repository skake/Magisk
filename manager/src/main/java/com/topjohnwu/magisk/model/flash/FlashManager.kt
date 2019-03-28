package com.topjohnwu.magisk.model.flash

import android.content.Context
import android.net.Uri
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import com.topjohnwu.magisk.model.su.WriteOnlyList
import com.topjohnwu.magisk.model.zip.Zip
import com.topjohnwu.magisk.ui.flash.IFlashLog
import com.topjohnwu.superuser.Shell
import io.reactivex.Single
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException


sealed class FlashManager {

    lateinit var context: Context
    lateinit var console: IFlashLog

    protected fun log(line: String) {
        if (this::console.isInitialized) {
            console.log(line)
        } else {
            Timber.i(line)
        }
    }

    protected fun checkSU() = Single.fromCallable {
        if (!Shell.rootAccess()) {
            log("! No root access")
            throw RuntimeException("Cannot flash without root")
        }
    }

    abstract operator fun invoke(): Single<FlashManager.Result>


    //region Initialization
    companion object {
        inline operator fun <reified FlashProcess : FlashManager> invoke(builder: FlashProcess.() -> Unit) =
            FlashProcess::class.java.newInstance().apply(builder).invoke()
    }
    //endregion


    //region Result
    data class Result(val isSuccess: Boolean)
    //endregion


    //region Children
    class Flash : FlashManager() {

        lateinit var source: Uri

        private val tmpFile by lazy { File(context.cacheDir, "install.zip") }
        private val resolver get() = context.contentResolver
        private val out = WriteOnlyList { log(it) }

        @AnyThread
        override fun invoke() = checkSU()
            .map { source }
            .map {
                log("- Copying zip to temp directory")
                resolver
                    .openInputStream(it)
                    .orThrow(FileNotFoundException("$it cannot be found"))
                    .copyTo(tmpFile.outputStream())
                log("- Copying zip successful")
                tmpFile
            }
            .map {
                log("- Unzipping binaries")
                Zip {
                    zip = it
                    destination = it.parentFile
                    path = "META-INF/com/google/android"
                    isPathTrashy = true
                }.unzip()
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
                tmpFile
            }

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

    class Uninstall : FlashManager() {

        lateinit var source: Uri

        override fun invoke(): Single<Result> = Single.error(NotImplementedError())
    }

    class PatchBoot : FlashManager() {

        lateinit var source: Uri

        override fun invoke(): Single<Result> = Single.error(NotImplementedError())
    }

    class InactiveSlot : FlashManager() {
        override fun invoke(): Single<Result> = Single.error(NotImplementedError())
    }

    class Magisk : FlashManager() {
        override fun invoke(): Single<Result> = Single.error(NotImplementedError())
    }
    //endregion

    protected fun <T> T?.orThrow(exception: Exception): T {
        if (this == null) {
            throw exception
        }
        return this
    }

    protected fun Boolean.toResult() = FlashManager.Result(this)

}
