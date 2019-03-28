package com.topjohnwu.magisk

import android.app.Application
import android.util.Log
import com.chibatching.kotpref.Kotpref
import com.topjohnwu.magisk.di.koinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

@Suppress("ConstantConditionIf")
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Kotpref.init(this)

        startKoin {
            androidContext(this@MyApplication)
            modules(koinModules)
        }

        if (Constants.DEBUG) {
            Timber.plant(debugTree)
        } else {
            Timber.plant(crashReportingTree)
        }
    }

    private val debugTree = Timber.DebugTree()

    private val crashReportingTree = object : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }

            //Log here to crashlytics
        }
    }
}
