package com.topjohnwu.magisk.di

import com.topjohnwu.magisk.data.repository.FilesRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { FilesRepository(get(), get()) }
}
