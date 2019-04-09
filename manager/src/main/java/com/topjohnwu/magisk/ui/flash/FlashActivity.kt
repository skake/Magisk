package com.topjohnwu.magisk.ui.flash

import android.view.KeyEvent
import androidx.core.os.bundleOf
import com.skoumal.teanity.viewmodel.LoadingViewModel
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.ActivityFlashBinding
import com.topjohnwu.magisk.model.navigation.action
import com.topjohnwu.magisk.model.navigation.data
import com.topjohnwu.magisk.ui.base.MagiskActivity
import com.topjohnwu.magisk.util.directions.FlashAction
import org.koin.androidx.viewmodel.ext.viewModel
import org.koin.core.parameter.parametersOf


class FlashActivity : MagiskActivity<FlashViewModel, ActivityFlashBinding>() {

    override val layoutRes: Int = R.layout.activity_flash
    override val viewModel: FlashViewModel by viewModel {
        val bundle = intent?.extras ?: bundleOf()
        val action = bundle.action ?: FlashAction.DIE
        val data = bundle.data
        val args = FlashActivityArgs.Builder(action, data).build()
        parametersOf(args)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_UP -> super.onKeyDown(keyCode, event).let { true }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onBackPressed() {
        if (viewModel.state != LoadingViewModel.State.LOADING) {
            super.onBackPressed()
        }
    }

}
