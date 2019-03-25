package com.topjohnwu.magisk.ui.flash

import android.os.Handler
import androidx.core.os.postDelayed
import com.skoumal.teanity.databinding.ComparableRvItem
import com.skoumal.teanity.util.DiffObservableList
import com.skoumal.teanity.util.KObservableField
import com.topjohnwu.magisk.BR
import com.topjohnwu.magisk.ui.base.MagiskViewModel
import com.topjohnwu.magisk.ui.events.ViewEvent
import me.tatarka.bindingcollectionadapter2.OnItemBind


class FlashViewModel : MagiskViewModel() {

    val showRestartTitle = KObservableField(false)
    val behaviorText = KObservableField("Flashing...")

    val lines = DiffObservableList(ComparableRvItem.callback)
    val itemBinding = OnItemBind<ComparableRvItem<*>> { itemBinding, _, item ->
        item.bind(itemBinding)
        itemBinding.bindExtra(BR.viewModel, this@FlashViewModel)
    }

    init {
        Handler().postDelayed(5000) {
            //dummy pretending to do work
            finishWork()
        }
    }

    fun backPressed() = ViewEvent.BACK_PRESS.publish()
    fun restartPressed() = Unit
    fun savePressed() = Unit

    private fun finishWork() {
        state = State.LOADED
        behaviorText.value = "Done!"
        Handler().postDelayed(500) {
            showRestartTitle.value = true
        }
    }

}