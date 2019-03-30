package com.topjohnwu.magisk.model.flash

import io.reactivex.Single


abstract class MagiskFlashInstaller : MagiskInstaller() {

    override fun construct(): Single<Any> = super.construct()
        .flatMap { MagiskFlasher.flash(installDir, boot) }

}
