package com.topjohnwu.magisk

import androidx.appcompat.app.AppCompatDelegate
import com.chibatching.kotpref.KotprefModel

object Config : KotprefModel() {
    override val kotprefName: String = "config"

    var darkMode by intPref(default = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, key = "darkMode")
}