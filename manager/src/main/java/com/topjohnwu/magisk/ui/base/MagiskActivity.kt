package com.topjohnwu.magisk.ui.base

import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.NavHostFragment
import com.skoumal.teanity.view.TeanityActivity
import com.skoumal.teanity.viewmodel.TeanityViewModel
import com.topjohnwu.magisk.Config
import com.topjohnwu.magisk.ui.events.ViewEvent


abstract class MagiskActivity<ViewModel : TeanityViewModel, Binding : ViewDataBinding> :
    TeanityActivity<ViewModel, Binding>() {

    protected val navHostFragment get() = supportFragmentManager.fragments.firstOrNull() as? NavHostFragment
    protected val fragments get() = navHostFragment?.childFragmentManager?.fragments.orEmpty().filterNotNull()

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        AppCompatDelegate.setDefaultNightMode(Config.darkMode)
    }

    @CallSuper
    override fun onSimpleEventDispatched(event: Int) {
        when (event) {
            ViewEvent.BACK_PRESS -> onBackPressed()
        }
    }

    override fun onBackPressed() {
        val fragmentsHandled = fragments.asSequence()
            .filter { it.userVisibleHint }
            .filterIsInstance<MagiskFragment<*, *>>()
            .toList()
            .any { it.onBackPressed() }

        if (fragmentsHandled) return

        super.onBackPressed()
    }
}