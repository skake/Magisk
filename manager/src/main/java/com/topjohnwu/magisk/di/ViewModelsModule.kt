package com.topjohnwu.magisk.di

import com.topjohnwu.magisk.ui.MainViewModel
import com.topjohnwu.magisk.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModules = module {
    viewModel { MainViewModel() }
    viewModel { HomeViewModel() }
}