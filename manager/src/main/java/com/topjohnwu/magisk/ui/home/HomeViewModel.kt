package com.topjohnwu.magisk.ui.home

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Handler
import androidx.core.os.postDelayed
import com.google.android.material.snackbar.Snackbar
import com.skoumal.teanity.databinding.ComparableRvItem
import com.skoumal.teanity.util.DiffObservableList
import com.skoumal.teanity.util.KObservableField
import com.skoumal.teanity.viewevents.SnackbarEvent
import com.topjohnwu.magisk.BR
import com.topjohnwu.magisk.BuildConfig
import com.topjohnwu.magisk.Config
import com.topjohnwu.magisk.data.repository.MagiskRepository
import com.topjohnwu.magisk.model.entity.SupportItem
import com.topjohnwu.magisk.model.entity.SupportRvItem
import com.topjohnwu.magisk.model.flash.MagiskInstallMethod
import com.topjohnwu.magisk.model.navigation.Navigation
import com.topjohnwu.magisk.model.observer.Observer
import com.topjohnwu.magisk.model.version.Version
import com.topjohnwu.magisk.ui.base.MagiskViewModel
import com.topjohnwu.magisk.ui.events.ViewEvent
import com.topjohnwu.magisk.util.assign
import com.topjohnwu.magisk.util.directions.FlashAction
import com.topjohnwu.magisk.util.launch
import com.topjohnwu.magisk.util.mv
import me.tatarka.bindingcollectionadapter2.OnItemBind
import java.io.File
import kotlin.random.Random

class HomeViewModel(
    private val magiskRepo: MagiskRepository,
    context: Context
) : MagiskViewModel() {

    object SafetyNetState {
        const val UNKNOWN = 0
        const val LOADING = 1
        const val LOADED = 2
    }

    val magiskCurrentVersion = KObservableField(Version("", -1))
    val magiskUpdateVersion = KObservableField(Version("", -1))
    val magiskVersionUpdate = Observer(magiskCurrentVersion, magiskUpdateVersion) {
        magiskCurrentVersion.value.versionCode < magiskUpdateVersion.value.versionCode
    }

    val appPackageName = KObservableField(context.packageName)
    val appCurrentVersion =
        KObservableField(Version(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE))
    val appUpdateVersion = KObservableField(Version("", -1))
    val appVersionUpdate = Observer(appCurrentVersion, appUpdateVersion) {
        appCurrentVersion.value.versionCode < appUpdateVersion.value.versionCode
    }

    val isForceEncryption = KObservableField(Config.forceEncrypt)
    val isAVB = KObservableField(Config.keepVerity)

    val safetyNetState = KObservableField(SafetyNetState.UNKNOWN)
    val isCTS = KObservableField(false)
    val isIntegrity = KObservableField(false)

    val items = DiffObservableList(ComparableRvItem.callback).apply {
        val newItems = SupportItem.values().map { SupportRvItem(it) }
        update(newItems)
    }
    val itemBinding = OnItemBind<ComparableRvItem<*>> { itemBinding, _, item ->
        item.bind(itemBinding)
        itemBinding.bindExtra(BR.viewModel, this@HomeViewModel)
    }

    init {
        magiskRepo.fetchMagiskVersion()
            .assign {
                onSuccess {
                    magiskCurrentVersion.value = it
                }
            }

        magiskRepo.fetchConfig()
            .applyViewModel(this)
            .assign {
                onSuccess {
                    it.magisk.apply {
                        val code = versionCode.toIntOrNull() ?: -1
                        magiskUpdateVersion.value = Version(version, code)
                    }
                    it.app.apply {
                        val code = versionCode.toIntOrNull() ?: BuildConfig.VERSION_CODE
                        appUpdateVersion.value = Version(version, code)
                    }
                }
            }
    }

    fun sheetBackPressed() = ViewEvent.BACK_PRESS.publish()

    fun changelogMagiskPressed() = Unit

    fun installMagiskPressed() = ViewEvent.NAVIGATION_INSTALL_MAGISK.publish()

    fun changelogManagerPressed() = Unit

    fun installManagerPressed() {}

    fun supportPressed(item: SupportItem) = item.url.launch()

    fun uninstallPressed() {}

    fun verifyPressed() {
        safetyNetState.value = SafetyNetState.LOADING

        Handler().postDelayed(3000) {
            val result = Random(System.currentTimeMillis()).nextBoolean()

            isCTS.value = result
            isIntegrity.value = result

            safetyNetState.value = SafetyNetState.LOADED
        }
    }

    fun installMagiskWithMethod(method: MagiskInstallMethod) = when (method) {
        MagiskInstallMethod.DOWNLOAD_ONLY -> downloadMagisk()

        MagiskInstallMethod.PATCH -> ViewEvent.NAVIGATION_SELECT_BOOT.publish()

        MagiskInstallMethod.INSTALL -> Navigation
            .flash(FlashAction.FLASH_MAGISK)
            .publish()

        MagiskInstallMethod.INACTIVE_SLOT -> Navigation
            .flash(FlashAction.FLASH_INACTIVE_SLOT)
            .publish()
    }

    private fun downloadMagisk() {
        magiskRepo.fetchMagisk().assign {
            onSuccess {
                val external = Environment.getExternalStorageDirectory().path
                val location = "$external/Magisk/magisk.zip"

                it.mv(File(location))

                SnackbarEvent("Magisk is saved @ $location", Snackbar.LENGTH_INDEFINITE) {
                    setAction("OK") { dismiss() }
                }.publish()
            }
        }
    }

    fun navigateToFlash(data: Uri?) = Navigation.flash(FlashAction.PATCH_BOOT, data).publish()

}
