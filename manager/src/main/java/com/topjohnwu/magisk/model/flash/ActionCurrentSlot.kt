package com.topjohnwu.magisk.model.flash

import io.reactivex.Single


class ActionCurrentSlot : MagiskInstaller() {

    override fun invoke(): Single<Result> = Single.error(NotImplementedError())

}
