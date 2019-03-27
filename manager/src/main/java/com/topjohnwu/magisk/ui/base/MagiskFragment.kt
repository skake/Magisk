package com.topjohnwu.magisk.ui.base

import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.skoumal.teanity.view.TeanityFragment
import com.skoumal.teanity.viewmodel.TeanityViewModel
import com.topjohnwu.magisk.ui.events.ViewEvent


abstract class MagiskFragment<ViewModel : TeanityViewModel, Binding : ViewDataBinding> :
    TeanityFragment<ViewModel, Binding>() {

    @CallSuper
    override fun onSimpleEventDispatched(event: Int) {
        when (event) {
            ViewEvent.BACK_PRESS -> onBackPressed()
        }
    }

    open fun onBackPressed(): Boolean = false
    open fun onPermissionsRejected(permissions: List<PermissionRequest>) = Unit
    open fun onPermissionRationaleRequested(permissions: List<PermissionRequest>) = Unit

    fun withPermissions(vararg permissions: String, callback: () -> Unit) =
        Dexter.withActivity(activity)
            .withPermissions(*permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted() == true) {
                        callback()
                    } else {
                        val rejectedPermissions = report?.deniedPermissionResponses
                            ?.map { it.requestedPermission }
                            ?: return
                        onPermissionsRejected(rejectedPermissions)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    onPermissionRationaleRequested(permissions ?: return)
                }
            })
            .check()

}
