package com.topjohnwu.magisk.model.flash

import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.io.SuFile
import io.reactivex.Single
import java.io.File


class ActionCurrentSlot : MagiskFlashInstaller() {

    override val boot: File by lazy {
        log("- Detecting target image")
        val path = ShellUtils.fastCmd("find_boot_image", "echo \"\$BOOTIMAGE\"")
        if (path.isEmpty()) {
            log("! Unable to detect target image")
            throw IllegalArgumentException("Unable to detect target image")
        }
        SuFile(path)
    }
    override val installDir: File by lazy { File(context.filesDir.parent, "install") }

    override fun invoke(): Single<Result> = construct()
}
