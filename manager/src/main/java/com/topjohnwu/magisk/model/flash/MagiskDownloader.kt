package com.topjohnwu.magisk.model.flash

import android.os.Build
import com.topjohnwu.magisk.Config
import com.topjohnwu.magisk.Constants
import com.topjohnwu.magisk.model.zip.Zip
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.io.SuFile
import io.reactivex.Single
import java.io.File


object MagiskDownloader {

    private const val x86 = "x86"
    private const val arm = "arm"

    private const val magiskinit64 = "magiskinit64"
    private const val magiskinit = "magiskinit"

    private val magiskZip by lazy { File(Config.context.cacheDir, Constants.MAGISK_FILENAME) }

    @Suppress("DEPRECATION")
    private val arch
        get() = if (Build.VERSION.SDK_INT >= 21) {
            if (Build.SUPPORTED_ABIS.contains(x86)) x86 else arm
        } else {
            if (Build.CPU_ABI == x86) x86 else arm
        }

    /** @return installDir location */
    fun download(installDir: File, downloader: Single<File>) = Single.just(magiskZip)
        .flatMap { if (it.exists() && it.matchesChecksum()) Single.just(it) else downloader }
        .map {
            it.unzipTo(installDir)
            installDir.ensureCorrectInit()
        }

    private fun File.ensureCorrectInit(): File {
        val init64 = SuFile(this, magiskinit64)
        if (Build.VERSION.SDK_INT >= 21 && Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()) {
            init64.renameTo(SuFile(this, magiskinit))
        } else {
            init64.delete()
        }
        return this
    }

    private fun File.unzipTo(installDir: File) = Zip {
        zip = this@unzipTo
        destination = installDir
    }.unzip(
        "$arch/" to true,
        "common/" to true,
        "META-INF/com/google/android/update-binary" to true,
        "chromeos/" to false
    )

    private fun File.matchesChecksum(checksum: String = Config.magiskChecksum) =
        ShellUtils.checkSum("MD5", this, checksum)

}
