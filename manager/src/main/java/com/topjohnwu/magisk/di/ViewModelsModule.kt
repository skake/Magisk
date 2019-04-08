package com.topjohnwu.magisk.di

import com.topjohnwu.magisk.ui.MainViewModel
import com.topjohnwu.magisk.ui.flash.FlashActivityArgs
import com.topjohnwu.magisk.ui.flash.FlashViewModel
import com.topjohnwu.magisk.ui.home.HomeViewModel
import com.topjohnwu.magisk.ui.log.LogViewModel
import com.topjohnwu.magisk.ui.module.ModuleViewModel
import com.topjohnwu.magisk.ui.settings.SettingsViewModel
import com.topjohnwu.magisk.ui.superuser.SuperuserViewModel
import com.topjohnwu.magisk.ui.surequest.SuperuserRequestViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { MainViewModel() }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { LogViewModel(get()) }
    viewModel { ModuleViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { SuperuserViewModel(get()) }
    viewModel { (data: FlashActivityArgs) -> FlashViewModel(data, get(), get()) }
    viewModel { SuperuserRequestViewModel() }
}
