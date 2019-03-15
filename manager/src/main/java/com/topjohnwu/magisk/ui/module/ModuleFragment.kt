package com.topjohnwu.magisk.ui.module

import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.FragmentModuleBinding
import com.topjohnwu.magisk.ui.base.MagiskFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class ModuleFragment : MagiskFragment<ModuleViewModel, FragmentModuleBinding>() {

    override val layoutRes: Int = R.layout.fragment_module
    override val viewModel: ModuleViewModel by viewModel()
}