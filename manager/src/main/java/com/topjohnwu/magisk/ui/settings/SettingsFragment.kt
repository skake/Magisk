package com.topjohnwu.magisk.ui.settings

import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.FragmentSettingsBinding
import com.topjohnwu.magisk.ui.base.MagiskFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : MagiskFragment<SettingsViewModel, FragmentSettingsBinding>() {

    override val layoutRes: Int = R.layout.fragment_settings
    override val viewModel: SettingsViewModel by viewModel()


}