package com.topjohnwu.magisk.model.flash

import com.topjohnwu.magisk.ui.flash.IFlashLog


abstract class MagiskLogger(
    private val console: IFlashLog
) {

    fun log(line: String) {
        console.log(line)
    }

}
