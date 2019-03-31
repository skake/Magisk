package com.topjohnwu.magisk.model.flash

import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.io.SuFile
import io.reactivex.Single
import java.io.File


class ActionInactiveSlot : MagiskFlashInstaller() {

    lateinit var bootctlDownloader: Single<File>

    override val boot: File by lazy {
        val slot = ShellUtils.fastCmd("echo \$SLOT")
        val target = if (slot == slot_A) slot_B else slot_A
        log("- Target slot: $target")
        log("- Detecting target image")
        val path = ShellUtils.fastCmd(
            "SLOT=$target",
            "find_boot_image",
            "SLOT=$slot",
            "echo \"\$BOOTIMAGE\""
        )
        if (path.isEmpty()) {
            log("! Unable to detect target image")
            throw IllegalArgumentException("Unable to detect target image")
        }
        SuFile(path)
    }
    override val installDir: File by lazy { File(context.filesDir.parent, "install") }

    override fun invoke(): Single<Result> = construct()
        .flatMap { bootctlDownloader }
        .map { it.copyTo(SuFile("/data/adb/bootctl")) }
        .map {
            Shell.su("post_ota ${it.parent}").exec().isSuccess.toResult().apply {
                log("***************************************")
                log(" Next reboot will boot to second slot!")
                log("***************************************")
            }
        }

    companion object {
        private const val slot_A = "_a"
        private const val slot_B = "_b"
    }

}
