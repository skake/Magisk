package com.topjohnwu.magisk.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager


fun ApplicationInfo.findAppLabel(pm: PackageManager): String {
    return pm.getApplicationLabel(this)?.toString().orEmpty()
}