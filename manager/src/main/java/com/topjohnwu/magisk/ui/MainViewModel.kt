package com.topjohnwu.magisk.ui

import com.topjohnwu.magisk.ui.base.MagiskViewModel
import com.topjohnwu.magisk.ui.events.ViewEvent

class MainViewModel : MagiskViewModel() {

    fun superuserPressed() = ViewEvent.NAVIGATION_SUPERUSER_PRESS.publish()

}