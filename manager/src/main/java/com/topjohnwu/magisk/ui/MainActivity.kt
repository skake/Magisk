package com.topjohnwu.magisk.ui

import com.skoumal.teanity.viewevents.ViewEvent
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.ActivityMainBinding
import com.topjohnwu.magisk.ui.base.MagiskActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : MagiskActivity<MainViewModel, ActivityMainBinding>() {

    override val layoutRes: Int = R.layout.activity_main
    override val viewModel: MainViewModel by viewModel()
    override val navHostId = R.id.main_nav_host

    override fun onEventDispatched(event: ViewEvent) {}
}
