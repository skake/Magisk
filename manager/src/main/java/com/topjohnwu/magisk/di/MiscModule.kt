package com.topjohnwu.magisk.di

import android.content.Context
import android.net.ConnectivityManager
import org.koin.dsl.module.module

val miscModule = module {

    single { get<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    single { get<Context>().packageManager }

}