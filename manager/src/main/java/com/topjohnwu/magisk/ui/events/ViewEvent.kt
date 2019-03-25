package com.topjohnwu.magisk.ui.events


object ViewEvent {

    const val BACK_PRESS = Int.MAX_VALUE
    const val NAVIGATION_SUPERUSER_PRESS = BACK_PRESS - 1
    const val DARK_MODE_PRESS = NAVIGATION_SUPERUSER_PRESS - 1
    const val FLASH = DARK_MODE_PRESS - 1 //only testing property

}