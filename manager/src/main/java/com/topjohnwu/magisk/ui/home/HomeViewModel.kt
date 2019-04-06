package com.topjohnwu.magisk.ui.home

import android.os.Handler
import androidx.core.os.postDelayed
import com.skoumal.teanity.databinding.ComparableRvItem
import com.skoumal.teanity.util.DiffObservableList
import com.skoumal.teanity.util.KObservableField
import com.topjohnwu.magisk.BR
import com.topjohnwu.magisk.model.entity.SupportItem
import com.topjohnwu.magisk.model.entity.SupportRvItem
import com.topjohnwu.magisk.ui.base.MagiskViewModel
import com.topjohnwu.magisk.ui.events.ViewEvent
import me.tatarka.bindingcollectionadapter2.OnItemBind
import kotlin.random.Random

class HomeViewModel : MagiskViewModel() {

    object SafetyNetState {
        const val UNKNOWN = 0
        const val LOADING = 1
        const val LOADED = 2
    }

    object VersionState {
        const val UNKNOWN = 0
        const val LOADING = 1
        const val LOADED = 2
    }

    val magiskVersionUpdate = KObservableField(false)
    val magiskVersionState = KObservableField(VersionState.LOADING)
    val magiskVersion = KObservableField("v18.1")
    val magiskVersionCode = KObservableField(18100)

    val appVersionUpdate = KObservableField(false)
    val appVersionState = KObservableField(VersionState.LOADING)
    val appVersion = KObservableField("v18.1")
    val appVersionCode = KObservableField(18100)
    val appPackageName = KObservableField("com.topjohnwu.magisk")

    val isForceEncryption = KObservableField(false /*fetch default*/)
    val isAVB = KObservableField(false /*fetch default*/)

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
        Handler().postDelayed(2000) {
            appVersionState.value = VersionState.LOADED
            appVersionUpdate.value = Random(System.currentTimeMillis()).nextBoolean()
        }

        Handler().postDelayed(4000) {
            magiskVersionState.value = VersionState.LOADED
            magiskVersionUpdate.value = Random(System.currentTimeMillis()).nextBoolean()
        }
    }

    fun sheetBackPressed() = ViewEvent.BACK_PRESS.publish()
    fun uninstallPressed() {}
    fun installMagiskPressed() {}
    fun installManagerPressed() {}
    fun supportPressed(item: SupportItem) {}

    fun verifyPressed() {
        safetyNetState.value = SafetyNetState.LOADING

        Handler().postDelayed(3000) {
            val result = Random(System.currentTimeMillis()).nextBoolean()

            isCTS.value = result
            isIntegrity.value = result

            safetyNetState.value = SafetyNetState.LOADED
        }
    }

}
