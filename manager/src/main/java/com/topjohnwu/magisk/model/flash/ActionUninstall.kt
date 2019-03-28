package com.topjohnwu.magisk.model.flash

import com.topjohnwu.superuser.Shell


class ActionUninstall : ActionFlash() {

    override fun invoke() = super.invoke()
        .doOnSuccess {
            if (it.isSuccess) {
                Shell.su("pm uninstall ${context.packageName}")
            }
        }
}
