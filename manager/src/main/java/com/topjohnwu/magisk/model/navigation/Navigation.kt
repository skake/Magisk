package com.topjohnwu.magisk.model.navigation

import android.net.Uri
import com.skoumal.teanity.viewevents.NavigationEvent
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.ui.events.ViewEvent
import com.topjohnwu.magisk.util.directions.FlashAction


object Navigation {

    fun back() = ViewEvent.BACK_PRESS

    fun flash(flashAction: FlashAction, target: Uri? = null) = NavigationEvent {
        navDirections { destination = R.id.flashActivity }
        args {
            action = flashAction
            data = target.toString()
        }
    }

}
