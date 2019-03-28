package com.topjohnwu.magisk.model.flash

import io.reactivex.Single


class ActionInactiveSlot : MagiskInstaller() {

    override fun invoke(): Single<Result> = Single.error(NotImplementedError())

}
