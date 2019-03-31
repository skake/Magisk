package com.topjohnwu.magisk.model.flash

import io.reactivex.Single
import java.io.File


abstract class MagiskFlashInstaller : MagiskInstaller() {

    private val flasher by lazy { MagiskFlasher(console) }

    override fun construct(): Single<File> = super.construct()
        .flatMap { flasher.flash(installDir, boot) }
        .map { boot } //this is stupid

}
