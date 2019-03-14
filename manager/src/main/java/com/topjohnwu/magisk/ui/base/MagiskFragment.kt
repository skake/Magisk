package com.topjohnwu.magisk.ui.base

import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
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

}