package com.topjohnwu.magisk.model.flash

import android.content.Context
import com.topjohnwu.magisk.ui.flash.IFlashLog
import com.topjohnwu.superuser.Shell
import io.reactivex.Single
import timber.log.Timber


abstract class FlashManager {

    lateinit var context: Context
    lateinit var console: IFlashLog

    protected fun log(line: String) {
        if (this::console.isInitialized) {
            console.log(line)
        } else {
            Timber.i(line)
        }
    }

    protected fun checkSU() = Single.fromCallable {
        if (!Shell.rootAccess()) {
            log("! No root access")
            throw RuntimeException("Cannot flash without root")
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

    protected fun <T> T?.orThrow(exception: Exception): T {
        if (this == null) {
            throw exception
        }
        return this
    }

    protected fun Boolean.toResult() = FlashManager.Result(this)

}
