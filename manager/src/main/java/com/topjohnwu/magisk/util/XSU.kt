package com.topjohnwu.magisk.util

import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFileOutputStream
import java.io.File


fun reboot(recovery: Boolean = false): Shell.Result {
    val command = StringBuilder("/system/bin/reboot")
        .appendIf(recovery) {
            append(" recovery")
        }
        .toString()

    return Shell.su(command).exec()
}

fun File.suOutputStream() = SuFileOutputStream(this)
