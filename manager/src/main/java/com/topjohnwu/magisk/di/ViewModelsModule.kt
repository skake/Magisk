package com.topjohnwu.magisk.di

import com.topjohnwu.magisk.ui.MainViewModel
import com.topjohnwu.magisk.ui.flash.FlashViewModel
import com.topjohnwu.magisk.ui.home.HomeViewModel
import com.topjohnwu.magisk.ui.log.LogViewModel
import com.topjohnwu.magisk.ui.module.ModuleViewModel
import com.topjohnwu.magisk.ui.settings.SettingsViewModel
import com.topjohnwu.magisk.ui.superuser.SuperuserViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModules = module {
    viewModel { MainViewModel() }
    viewModel { HomeViewModel() }
    viewModel { LogViewModel() }
    viewModel { ModuleViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { SuperuserViewModel(get()) }
    viewModel { FlashViewModel() }
}