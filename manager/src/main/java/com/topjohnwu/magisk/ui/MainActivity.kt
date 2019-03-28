package com.topjohnwu.magisk.ui

import android.os.Bundle
import androidx.navigation.ui.setupWithNavController
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.ActivityMainBinding
import com.topjohnwu.magisk.ui.base.MagiskActivity
import com.topjohnwu.magisk.ui.events.ViewEvent
import org.koin.androidx.viewmodel.ext.viewModel

class MainActivity : MagiskActivity<MainViewModel, ActivityMainBinding>() {

    override val layoutRes: Int = R.layout.activity_main
    override val viewModel: MainViewModel by viewModel()
    override val navHostId = R.id.main_nav_host

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding.mainBottomBar) {
            setupWithNavController(navController)
            //prevents nav reacting to re-press
            setOnNavigationItemReselectedListener {}
        }
    }

    override fun onSimpleEventDispatched(event: Int) {
        super.onSimpleEventDispatched(event)

        when (event) {
            ViewEvent.NAVIGATION_SUPERUSER_PRESS -> navigateToSuperuser()
        }
    }

    private fun navigateToSuperuser() {
        binding.mainBottomBar.selectedItemId = R.id.superuserFragment
    }

}
