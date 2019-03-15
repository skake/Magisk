package com.topjohnwu.magisk.ui.log

import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.FragmentLogBinding
import com.topjohnwu.magisk.ui.base.MagiskFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class LogFragment : MagiskFragment<LogViewModel, FragmentLogBinding>() {

    override val layoutRes: Int = R.layout.fragment_log
    override val viewModel: LogViewModel by viewModel()

}