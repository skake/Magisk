package com.topjohnwu.magisk.model.flash

import io.reactivex.Single
import java.io.File


abstract class MagiskFlashInstaller : MagiskInstaller() {

    override fun construct(): Single<File> = super.construct()
        .flatMap { MagiskFlasher.flash(installDir, boot) }
        .map { boot } //this is stupid

}
