package com.topjohnwu.magisk.ui.home

import com.skoumal.teanity.view.TeanityFragment
import com.topjohnwu.magisk.R
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import com.topjohnwu.magisk.databinding.FragmentHomeBinding

class HomeFragment : TeanityFragment<HomeViewModel, FragmentHomeBinding>() {

    override val layoutRes: Int = R.layout.fragment_home
    override val viewModel: HomeViewModel by sharedViewModel()

}