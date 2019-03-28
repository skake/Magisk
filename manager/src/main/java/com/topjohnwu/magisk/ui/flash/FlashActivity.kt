package com.topjohnwu.magisk.ui.flash

import android.view.KeyEvent
import androidx.core.os.bundleOf
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.ActivityFlashBinding
import com.topjohnwu.magisk.ui.base.MagiskActivity
import org.koin.androidx.viewmodel.ext.viewModel
import org.koin.core.parameter.parametersOf


class FlashActivity : MagiskActivity<FlashViewModel, ActivityFlashBinding>() {

    override val layoutRes: Int = R.layout.activity_flash
    override val viewModel: FlashViewModel by viewModel {
        val args = FlashActivityArgs.fromBundle(intent?.extras ?: bundleOf())
        parametersOf(args)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_UP -> super.onKeyDown(keyCode, event).let { true }
            else -> super.onKeyDown(keyCode, event)
        }
    }

}
