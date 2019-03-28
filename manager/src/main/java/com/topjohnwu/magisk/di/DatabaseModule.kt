package com.topjohnwu.magisk.di

import android.content.Context
import androidx.room.Room
import com.topjohnwu.magisk.data.database.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single { createDatabase(get()) }
}

fun createDatabase(context: Context): AppDatabase =
    Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME).build()
