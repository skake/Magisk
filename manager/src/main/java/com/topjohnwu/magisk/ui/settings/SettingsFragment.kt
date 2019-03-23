package com.topjohnwu.magisk.ui.settings

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.topjohnwu.magisk.Config
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.FragmentSettingsBinding
import com.topjohnwu.magisk.ui.base.MagiskFragment
import com.topjohnwu.magisk.ui.events.ViewEvent.DARK_MODE_PRESS
import com.topjohnwu.magisk.util.addOnGlobalLayoutListener
import com.topjohnwu.magisk.view.MagiskDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.max

class SettingsFragment : MagiskFragment<SettingsViewModel, FragmentSettingsBinding>() {

    override val layoutRes: Int = R.layout.fragment_settings
    override val viewModel: SettingsViewModel by viewModel()

    private val bottomSheet get() = BottomSheetBehavior.from(binding.settingsSheet)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSheet()
    }

    override fun onSimpleEventDispatched(event: Int) {
        super.onSimpleEventDispatched(event)

        when (event) {
            DARK_MODE_PRESS -> showDarkModeDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateDarkMode()
    }

    private fun setUpSheet() {
        binding.settingsParent.addOnGlobalLayoutListener {
            with(binding) {
                val offset = settingsParent.measuredHeight - settingsGlance.bottom
                bottomSheet.peekHeight = max(100, offset)
            }
        }
    }

    private fun showDarkModeDialog() {
        MagiskDialog(requireContext())
            .applyIcon(R.drawable.ic_dark_mode)
            .applyTitle(Html.fromHtml("Which <b>Dark Mode</b> settings does your heart desire?"))
            .applyButton(MagiskDialog.ButtonType.POSITIVE) {
                icon = R.drawable.ic_dark_mode
                title = "On at all times"
                onClick { updateDarkMode(AppCompatDelegate.MODE_NIGHT_YES) }
            }
            .applyButton(MagiskDialog.ButtonType.NEUTRAL) {
                icon = R.drawable.ic_dark_mode_off
                title = "Off at all times"
                onClick { updateDarkMode(AppCompatDelegate.MODE_NIGHT_NO) }
            }
            .applyButton(MagiskDialog.ButtonType.NEGATIVE) {
                icon = R.drawable.ic_dark_mode_auto
                title = "Turn on/off based on time of the day"
                onClick { updateDarkMode(AppCompatDelegate.MODE_NIGHT_AUTO) }
            }
            .applyButton(MagiskDialog.ButtonType.IDGAF) {
                icon = R.drawable.id_dark_mode_system
                title = "Follow system settings"
                onClick { updateDarkMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }
            }
            .show()
    }

    private fun updateDarkMode(mode: Int) {
        Config.darkMode = mode
        activity?.recreate()
    }

}