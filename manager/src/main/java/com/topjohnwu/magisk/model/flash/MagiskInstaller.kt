package com.topjohnwu.magisk.model.flash

import io.reactivex.Single
import java.io.File


abstract class MagiskInstaller : FlashManager() {

    abstract val boot: File
    abstract val installDir: File

    lateinit var magiskDownloader: Single<File>

    protected open fun construct(): Single<FlashManager.Result> = MagiskDownloader
        .download(installDir, magiskDownloader)
        .flatMap { MagiskPatcher.patch(it, boot) }
        .map { it.exists().toResult() }

}
