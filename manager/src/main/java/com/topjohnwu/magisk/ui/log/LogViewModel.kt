package com.topjohnwu.magisk.ui.log

import com.topjohnwu.magisk.ui.base.MagiskViewModel
import com.topjohnwu.magisk.ui.events.ViewEvent


class LogViewModel : MagiskViewModel() {

    fun sheetBackPressed() = ViewEvent.BACK_PRESS.publish()

}