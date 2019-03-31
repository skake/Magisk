package com.topjohnwu.magisk.model.flash

import com.topjohnwu.magisk.Config
import com.topjohnwu.magisk.ui.flash.IFlashLog
import com.topjohnwu.superuser.Shell
import io.reactivex.Single
import java.io.File


class MagiskFlasher(console: IFlashLog) : MagiskLogger(console) {

    /** @return result of flashing process. If Config.keepVerity is true, result will always be true, otherwise patch_dtbo_image result is returned */
    fun flash(installDir: File, boot: File) = Single.just(Shell.su())
        .map {
            log("- Flashing to current slot")
            val isFlashSuccessful = it.add("direct_install $installDir $boot")
                .exec()
                .isSuccess

            if (!isFlashSuccessful) {
                log("! Flashing to current slot was unsuccessful")
                throw IllegalStateException("Flashing boot has gone wrong")
            }
        }
        .map {
            if (!Config.keepVerity) {
                log("- Patching after flash")
                Shell.su("patch_dtbo_image").exec().isSuccess
            } else {
                true
            }
        }
        .map { FlashManager.Result(it) }

}
