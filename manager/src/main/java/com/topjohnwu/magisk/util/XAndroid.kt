package com.topjohnwu.magisk.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File


fun ApplicationInfo.findAppLabel(pm: PackageManager): String {
    return pm.getApplicationLabel(this)?.toString().orEmpty()
}

fun Intent.startActivity(context: Context) = context.startActivity(this)

fun File.provide(): Uri {
    val context: Context by inject()
    return FileProvider.getUriForFile(context, "com.topjohnwu.magisk.fileprovider", this)
}

fun File.mv(destination: File) {
    inputStream().copyTo(destination)
    deleteRecursively()
}

fun String.toFile() = File(this)

fun Intent.chooser(title: String = "Pick an app") = Intent.createChooser(this, title)
