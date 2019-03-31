package com.topjohnwu.magisk.model.flash

import io.reactivex.Single
import java.io.File


abstract class MagiskInstaller : FlashManager() {

    abstract val boot: File
    abstract val installDir: File

    lateinit var magiskDownloader: Single<File>

    private val downloader by lazy { MagiskDownloader(console) }
    private val patcher by lazy { MagiskPatcher(console) }

    protected open fun construct(): Single<File> = downloader.download(installDir, magiskDownloader)
        .flatMap { patcher.patch(it, boot) }

}
