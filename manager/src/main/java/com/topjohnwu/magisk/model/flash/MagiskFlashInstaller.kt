package com.topjohnwu.magisk.model.flash

import io.reactivex.Single


abstract class MagiskFlashInstaller : MagiskInstaller() {

    override fun construct(): Single<FlashManager.Result> = super.construct()
        .flatMap { MagiskFlasher.flash(installDir, boot) }

}
