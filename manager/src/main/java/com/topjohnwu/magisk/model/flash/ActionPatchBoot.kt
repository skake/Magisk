package com.topjohnwu.magisk.model.flash

import android.net.Uri
import io.reactivex.Single


class ActionPatchBoot : FlashManager() {

    lateinit var source: Uri

    override fun invoke(): Single<Result> = Single.error(NotImplementedError())

}
