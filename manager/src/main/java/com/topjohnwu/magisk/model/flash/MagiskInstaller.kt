package com.topjohnwu.magisk.model.flash

import android.os.Build
import com.topjohnwu.magisk.Config
import com.topjohnwu.magisk.Constants
import com.topjohnwu.magisk.model.su.WriteOnlyList
import com.topjohnwu.magisk.model.zip.Zip
import com.topjohnwu.magisk.util.suInputStream
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import io.reactivex.Single
import java.io.File


abstract class MagiskInstaller : FlashManager() {

    private val x86 = "x86"
    private val arm = "arm"

    private val magiskinit64 = "magiskinit64"
    private val magiskinit = "magiskinit"

    private val newBoot = "new-boot.img"
    private val signed = "signed.img"

    lateinit var magiskDownloader: Single<File>
    abstract val boot: File
    abstract val installDir: File

    private val magiskZip by lazy { File(context.cacheDir, Constants.MAGISK_FILENAME) }
    private val out = WriteOnlyList { log(it) }

    @Suppress("DEPRECATION")
    private val arch
        get() = if (Build.VERSION.SDK_INT >= 21) {
            if (Build.SUPPORTED_ABIS.contains(x86)) x86 else arm
        } else {
            if (Build.CPU_ABI == x86) x86 else arm
        }

    override fun invoke() = Single.just(magiskZip)
        .flatMap { if (it.exists() && it.matchesChecksum()) Single.just(it) else magiskDownloader }
        .map {
            it.unzip()
            installDir
        }
        .map {
            it.ensureCorrectInit()
            boot
        }
        .map {
            val isSigned = it.verifySignature()
            if (isSigned) {
                log("- Boot image is signed with AVB 1.0")
            }

            val isPatchSuccessful = it.patch(installDir)

            if (!isPatchSuccessful) {
                throw IllegalAccessException("Cannot continue, patching boot failed.")
            }
            isSigned
        }
        .map {
            val job = patchCleanup()
            if (it) {
                signPatched(job)
            }
            job.exec().isSuccess.toResult()
        }

    private fun File.unzip() = Zip {
        zip = this@unzip
        destination = installDir
    }.unzip(
        "$arch/" to true,
        "common/" to true,
        "META-INF/com/google/android/update-binary" to true,
        "chromeos/" to false
    )

    private fun File.ensureCorrectInit() {
        val init64 = SuFile(this, magiskinit64)
        if (Build.VERSION.SDK_INT >= 21 && Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()) {
            init64.renameTo(SuFile(this, magiskinit))
        } else {
            init64.delete()
        }
    }

    private fun File.patch(installDir: File) = Shell
        .sh(
            "cd $installDir",
            "KEEPFORCEENCRYPT=${Config.forceEncrypt} " +
                    "KEEPVERITY=${Config.keepVerity} " +
                    "sh update-binary sh boot_patch.sh $this"
        )
        .to(out, out)
        .exec()
        .isSuccess

    private fun patchCleanup() = Shell.sh(
        "./magiskboot --cleanup",
        "mv bin/busybox busybox",
        "rm -rf magisk.apk bin boot.img update-binary",
        "cd /"
    )

    private fun signPatched(job: Shell.Job) {
        log("- Signing boot image with test keys")
        val patched = File(installDir, newBoot).suInputStream()
        val signed = File(installDir, signed).outputStream()
        TODO("Sign 'signed' stream")
        @Suppress("UNREACHABLE_CODE")
        job.add("mv -f $signed $patched")
    }

    private fun File.verifySignature(): Boolean {
        TODO()
    }

    private fun File.matchesChecksum(checksum: String = Config.magiskChecksum): Boolean {
        TODO()
    }

}
