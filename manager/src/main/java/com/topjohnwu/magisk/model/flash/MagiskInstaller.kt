package com.topjohnwu.magisk.model.flash

import io.reactivex.Single


abstract class MagiskInstaller : FlashManager() {

    override fun invoke(): Single<Result> = Single.error(NotImplementedError())

}
