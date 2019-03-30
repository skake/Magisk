package com.topjohnwu.magisk.model.flash

import com.topjohnwu.magisk.Config
import com.topjohnwu.magisk.model.su.WriteOnlyList
import com.topjohnwu.magisk.util.suInputStream
import com.topjohnwu.signing.SignBoot
import com.topjohnwu.superuser.Shell
import io.reactivex.Single
import java.io.File


object MagiskPatcher {

    private const val newBoot = "new-boot.img"
    private const val signed = "signed.img"

    private val out = WriteOnlyList { /*log(it)*/ }

    /** @return new-boot.img location */
    fun patch(installDir: File, boot: File) = Single.just(installDir to boot)
        .map {
            val (dir, boot) = it

            val isPatchSuccessful = boot
                .patch(dir)
                .cleanup()
                .exec()
                .isSuccess

            if (!isPatchSuccessful) {
                throw IllegalAccessException("Cannot continue, patching boot failed.")
            }

            File(dir, newBoot).signIfVerified(boot)
        }

    private fun Shell.Job.cleanup() = add(
        "./magiskboot --cleanup",
        "mv bin/busybox busybox",
        "rm -rf magisk.apk bin "/*boot.img*/ + " update-binary"//,
        //"cd /"
    )

    private fun File.patch(installDir: File) = Shell.sh(
        "cd $installDir",
        "KEEPFORCEENCRYPT=${Config.forceEncrypt} " +
                "KEEPVERITY=${Config.keepVerity} " +
                "sh update-binary sh boot_patch.sh $this"
    )

    private fun File.signIfVerified(previousBoot: File): File = apply {
        val job = Shell.sh("rm -rf boot.img")

        if (previousBoot.verifySignature()) {
            val signedFile = File(parentFile, signed)
            val patchedStream = suInputStream()
            val signedStream = outputStream()

            SignBoot.doSignature("/boot", patchedStream, signedStream, null, null)

            job.add("mv -f $signedFile $this")
        }

        job.add("cd /").exec()
    }

    private fun File.verifySignature() = SignBoot.verifySignature(suInputStream(), null)
}
