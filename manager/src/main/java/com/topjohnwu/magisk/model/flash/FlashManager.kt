package com.topjohnwu.magisk.model.flash

import android.net.Uri
import com.topjohnwu.magisk.ui.flash.IFlashLog
import io.reactivex.Single
import timber.log.Timber


sealed class FlashManager {

    lateinit var console: IFlashLog

    protected fun log(line: String) {
        if (this::console.isInitialized) {
            console.log(line)
        } else {
            Timber.i(line)
        }
    }

    abstract operator fun invoke(): Single<FlashManager.Result>


    //region Initialization
    companion object {
        inline operator fun <reified FlashProcess : FlashManager> invoke(builder: FlashProcess.() -> Unit) =
            FlashProcess::class.java.newInstance().apply(builder).invoke()
    }
    //endregion


    //region Result
    data class Result(val isSuccess: Boolean)
    //endregion


    //region Children
    class Flash : FlashManager() {

        lateinit var source: Uri

        override fun invoke(): Single<Result> = Single.error(NotImplementedError())
    }

    class Uninstall : FlashManager() {

        lateinit var source: Uri

        override fun invoke(): Single<Result> = Single.error(NotImplementedError())
    }

    class PatchBoot : FlashManager() {

        lateinit var source: Uri

        override fun invoke(): Single<Result> = Single.error(NotImplementedError())
    }

    class InactiveSlot : FlashManager() {
        override fun invoke(): Single<Result> = Single.error(NotImplementedError())
    }

    class Magisk : FlashManager() {
        override fun invoke(): Single<Result> = Single.error(NotImplementedError())
    }

    //endregion
}
