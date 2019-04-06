package com.topjohnwu.magisk.data.repository

import android.content.Context
import com.topjohnwu.magisk.Config
import com.topjohnwu.magisk.data.database.base.suRaw
import com.topjohnwu.magisk.data.network.GithubRawApiServices
import com.topjohnwu.magisk.model.version.Version
import com.topjohnwu.magisk.util.writeToFile
import io.reactivex.Single
import io.reactivex.functions.BiFunction


class MagiskRepository(
    private val context: Context,
    private val apiRaw: GithubRawApiServices
) {

    /*
    * These values are set in stone for the whole time app is running, hence it will never return
    * updates, hence refresh is not necessary in the UI.
    *
    * The idea of caching these values by hand is completely wrong and there shouldn't be any scenario
    * where we should need a "force" refresh nevertheless.
    *
    * Auto checking for updates, done properly, should reinitialize whole app as it'd probably
    * be killed by the system.
    * */

    private val config = apiRaw.fetchConfig().cache()
    private val configBeta = apiRaw.fetchBetaConfig().cache()
    private val configCanary = apiRaw.fetchCanaryConfig().cache()
    private val configCanaryDebug = apiRaw.fetchCanaryDebugConfig().cache()


    fun fetchMagisk() = fetchConfig()
        .flatMap { apiRaw.fetchFile(it.magisk.link) }
        .map { it.writeToFile(context, FILE_MAGISK_ZIP) }

    fun fetchManager() = fetchConfig()
        .flatMap { apiRaw.fetchFile(it.app.link) }
        .map { it.writeToFile(context, FILE_MAGISK_APK) }

    fun fetchUninstaller() = fetchConfig()
        .flatMap { apiRaw.fetchFile(it.uninstaller.link) }
        .map { it.writeToFile(context, FILE_UNINSTALLER_ZIP) }

    fun fetchSafetynet() = apiRaw
        .fetchSafetynet()
        .map { it.writeToFile(context, FILE_SAFETY_NET_APK) }

    fun fetchBootctl() = apiRaw
        .fetchBootctl()
        .map { it.writeToFile(context, FILE_BOOTCTL_SH) }


    fun fetchConfig() = when (Config.updateChannel) {
        Config.UpdateChannel.STABLE -> config
        Config.UpdateChannel.BETA -> configBeta
        Config.UpdateChannel.CANARY -> configCanary
        Config.UpdateChannel.CANARY_DEBUG -> configCanaryDebug
    }


    fun fetchMagiskVersion(): Single<Version> = Single.zip(
        fetchMagiskVersionName(),
        fetchMagiskVersionCode(),
        BiFunction { versionName, versionCode ->
            Version(versionName, versionCode)
        }
    )

    private fun fetchMagiskVersionName() = "magisk -v".suRaw()
        .map { it.first() }
        .map { it.substring(0 until it.indexOf(":")) }
        .onErrorReturn { "Unknown" }

    private fun fetchMagiskVersionCode() = "magisk -V".suRaw()
        .map { it.first() }
        .map { it.toIntOrNull() ?: -1 }
        .onErrorReturn { -1 }

    companion object {
        const val FILE_MAGISK_ZIP = "magisk.zip"
        const val FILE_MAGISK_APK = "magisk.apk"
        const val FILE_UNINSTALLER_ZIP = "uninstaller.zip"
        const val FILE_SAFETY_NET_APK = "safetynet.apk"
        const val FILE_BOOTCTL_SH = "bootctl"
    }

}
