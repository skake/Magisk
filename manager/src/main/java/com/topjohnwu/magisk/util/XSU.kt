package com.topjohnwu.magisk.util

import com.topjohnwu.superuser.Shell


fun reboot(recovery: Boolean = false): Shell.Job {
    val command = StringBuilder("/system/bin/reboot")
        .appendIf(recovery) {
            append(" recovery")
        }
        .toString()

    return Shell.su(command)
}
