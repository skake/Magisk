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