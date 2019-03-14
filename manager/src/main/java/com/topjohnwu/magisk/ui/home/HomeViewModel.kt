package com.topjohnwu.magisk.ui.home

import com.skoumal.teanity.databinding.ComparableRvItem
import com.skoumal.teanity.util.DiffObservableList
import com.topjohnwu.magisk.BR
import com.topjohnwu.magisk.model.entity.SupportItem
import com.topjohnwu.magisk.model.entity.SupportRvItem
import com.topjohnwu.magisk.ui.base.MagiskViewModel
import com.topjohnwu.magisk.ui.events.ViewEvent
import me.tatarka.bindingcollectionadapter2.OnItemBind

class HomeViewModel : MagiskViewModel() {

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

}