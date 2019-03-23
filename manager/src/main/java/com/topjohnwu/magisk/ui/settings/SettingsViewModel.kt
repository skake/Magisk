package com.topjohnwu.magisk.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import com.skoumal.teanity.util.KObservableField
import com.topjohnwu.magisk.Config
import com.topjohnwu.magisk.ui.base.MagiskViewModel
import com.topjohnwu.magisk.ui.events.ViewEvent


class SettingsViewModel : MagiskViewModel() {

    val currentDarkMode = KObservableField(getDarkModeString())

    fun updateDarkMode() {
        currentDarkMode.value = getDarkModeString()
    }

    private fun getDarkModeString(): String = when (Config.darkMode) {
        AppCompatDelegate.MODE_NIGHT_YES -> "On at all times"
        AppCompatDelegate.MODE_NIGHT_NO -> "Off at all times"
        AppCompatDelegate.MODE_NIGHT_AUTO -> "Turn on/off based on time of the day"
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> "Follow system settings"
        else -> "Undefined"
    }

    fun darkModePressed() = ViewEvent.DARK_MODE_PRESS.publish()

}