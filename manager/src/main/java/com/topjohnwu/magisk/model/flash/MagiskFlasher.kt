package com.topjohnwu.magisk.model.flash

import com.topjohnwu.magisk.Config
import com.topjohnwu.superuser.Shell
import io.reactivex.Single
import java.io.File


object MagiskFlasher {

    /** @return result of flashing process. If Config.keepVerity is true, result will always be true, otherwise patch_dtbo_image result is returned */
    fun flash(installDir: File, boot: File) = Single.just(Shell.su())
        .map {
            val isFlashSuccessful = it.add("direct_install $installDir $boot")
                .exec()
                .isSuccess

            if (!isFlashSuccessful) throw IllegalStateException("Flashing boot has gone wrong")
        }
        .map {
            if (!Config.keepVerity) {
                Shell.su("patch_dtbo_image").exec().isSuccess
            } else {
                true
            }
        }

}
