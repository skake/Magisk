package com.topjohnwu.magisk

import androidx.appcompat.app.AppCompatDelegate
import com.chibatching.kotpref.KotprefModel

object Config : KotprefModel() {
    override val kotprefName: String = "config"

    var darkMode by intPref(default = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, key = "darkMode")
    val magiskChecksum by stringPref("", "magiskChecksum")
    val forceEncrypt by booleanPref(false, "forceEncryption")
    val keepVerity by booleanPref(false, "keepVerity")
    val isCanary by booleanPref(false, "isCanary")
    val isBeta by booleanPref(false, "isBeta")
    val bootFormat by stringPref("img", "bootFormat")

    val isStable get() = !(isCanary || isBeta)
}
