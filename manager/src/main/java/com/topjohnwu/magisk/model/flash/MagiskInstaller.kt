package com.topjohnwu.magisk.model.flash

import io.reactivex.Single
import java.io.File


abstract class MagiskInstaller : FlashManager() {

    abstract val boot: File
    abstract val installDir: File

    lateinit var magiskDownloader: Single<File>

    override fun invoke() = MagiskDownloader.download(installDir, magiskDownloader)
        .flatMap { MagiskPatcher.patch(it, boot) }
        .map { TODO(); Result(false) }

}
