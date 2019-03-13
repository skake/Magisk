package com.topjohnwu.magisk.ui

import android.os.Bundle
import com.skoumal.teanity.view.TeanityActivity
import com.skoumal.teanity.viewevents.ViewEvent
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.ActivityMainBinding
import com.topjohnwu.magisk.util.setupWith
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : TeanityActivity<MainViewModel, ActivityMainBinding>() {

    override val layoutRes: Int = R.layout.activity_main
    override val viewModel: MainViewModel by viewModel()
    override val navHostId = R.id.main_nav_host

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onEventDispatched(event: ViewEvent) {}
}
