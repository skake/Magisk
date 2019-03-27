package com.topjohnwu.magisk

import android.os.Environment
import android.os.Process
import java.io.File


object Constants {

    const val DEBUG = BuildConfig.BUILD_TYPE == "debug"
    const val ALPHA = BuildConfig.BUILD_TYPE == "alpha"
    const val BETA = BuildConfig.BUILD_TYPE == "beta"
    const val RELEASE = BuildConfig.BUILD_TYPE == "release"

    private const val API_URL_ALPHA = "https://example.com"
    private const val API_URL_BETA = "https://example.com"
    private const val API_URL_RELEASE = "https://example.com"

    val API_URL = when {
        RELEASE -> API_URL_RELEASE
        BETA -> API_URL_BETA
        else -> API_URL_ALPHA
    }

    // APK content
    val ANDROID_MANIFEST = "AndroidManifest.xml"

    val SU_KEYSTORE_KEY = "su_key"

    // Paths
    val MAGISK_PATH = "/sbin/.magisk/img"
    val EXTERNAL_PATH: File =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .apply { mkdirs() }
    val MAGISK_DISABLE_FILE: File = File("xxx")

    val BUSYBOX_PATH = "/sbin/.magisk/busybox"
    val TMP_FOLDER_PATH = "/dev/tmp"
    val MAGISK_LOG = "/cache/magisk.log"
    val MANAGER_CONFIGS = ".tmp.magisk.config"

    // Versions
    val UPDATE_SERVICE_VER = 1
    val MIN_MODULE_VER = 1500
    val SNET_EXT_VER = 12

    val USER_ID = Process.myUid() / 100000

    val MAGISK_LOG_FILENAME = "magisk_install_log_%s.log"

    object MAGISK_VER {
        val MIN_SUPPORT = 18000
    }

    object ID {
        val UPDATE_SERVICE_ID = 1
        val FETCH_ZIP = 2
        val SELECT_BOOT = 3
        val ONBOOT_SERVICE_ID = 6

        // notifications
        val MAGISK_UPDATE_NOTIFICATION_ID = 4
        val APK_UPDATE_NOTIFICATION_ID = 5
        val DTBO_NOTIFICATION_ID = 7
        val HIDE_MANAGER_NOTIFICATION_ID = 8
        val UPDATE_NOTIFICATION_CHANNEL = "update"
        val PROGRESS_NOTIFICATION_CHANNEL = "progress"
        val CHECK_MAGISK_UPDATE_WORKER_ID = "magisk_update"
    }

    object Url {
        val STABLE_URL = getRaw("master", "stable.json")
        val BETA_URL = getRaw("master", "beta.json")
        val CANARY_URL = getRaw("master", "canary_builds/release.json")
        val CANARY_DEBUG_URL = getRaw("master", "canary_builds/canary.json")
        val REPO_URL =
            "https://api.github.com/users/Magisk-Modules-Repo/repos?per_page=100&sort=pushed&page=%d"
        val FILE_URL = "https://raw.githubusercontent.com/Magisk-Modules-Repo/%s/master/%s"
        val ZIP_URL = "https://github.com/Magisk-Modules-Repo/%s/archive/master.zip"
        val MODULE_INSTALLER =
            "https://raw.githubusercontent.com/topjohnwu/Magisk/master/scripts/module_installer.sh"
        val PAYPAL_URL = "https://www.paypal.me/topjohnwu"
        val PATREON_URL = "https://www.patreon.com/topjohnwu"
        val TWITTER_URL = "https://twitter.com/topjohnwu"
        val XDA_THREAD = "http://forum.xda-developers.com/showthread.php?t=3432382"
        val SOURCE_CODE_URL = "https://github.com/topjohnwu/Magisk"
        val SNET_URL = getRaw("b66b1a914978e5f4c4bbfd74a59f4ad371bac107", "snet.apk")
        val BOOTCTL_URL = getRaw("9c5dfc1b8245c0b5b524901ef0ff0f8335757b77", "bootctl")
        private fun getRaw(where: String, name: String): String {
            return String.format(
                "https://raw.githubusercontent.com/topjohnwu/magisk_files/%s/%s",
                where,
                name
            )
        }
    }

    object Key {
        // others
        val LINK_KEY = "Link"
        val IF_NONE_MATCH = "If-None-Match"
        // intents
        val FROM_SPLASH = "splash"
        val OPEN_SECTION = "section"
        val INTENT_SET_NAME = "filename"
        val INTENT_SET_LINK = "link"
        val FLASH_SET_BOOT = "boot"
        val BROADCAST_MANAGER_UPDATE = "manager_update"
        val BROADCAST_REBOOT = "reboot"
    }

}
