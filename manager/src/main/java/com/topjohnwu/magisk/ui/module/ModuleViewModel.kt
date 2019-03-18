package com.topjohnwu.magisk.ui.module

import com.skoumal.teanity.databinding.ComparableRvItem
import com.skoumal.teanity.util.DiffObservableList
import com.topjohnwu.magisk.BR
import com.topjohnwu.magisk.model.entity.ModuleInstalledRvItem
import com.topjohnwu.magisk.model.entity.ModuleItem
import com.topjohnwu.magisk.ui.base.MagiskViewModel
import me.tatarka.bindingcollectionadapter2.OnItemBind
import kotlin.random.Random


class ModuleViewModel : MagiskViewModel() {

    val items = DiffObservableList(ComparableRvItem.callback).apply {
        val r = Random(System.currentTimeMillis())
        val newItems = (0..10).map {
            ModuleItem(
                "Super module",
                "1.5",
                "diareuse",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur ac turpis sit amet nibh gravida elementum eu at felis. Maecenas volutpat augue diam, in dictum dui faucibus at. Donec viverra, velit vel ultricies commodo, felis libero luctus augue, sit amet molestie magna turpis eu mauris. Ut consequat, lorem et consequat placerat, est dolor mattis orci, at ultricies ligula neque vel lacus. Vivamus vitae nisl fringilla, fringilla purus at, pellentesque odio. Suspendisse at tempor sapien. Duis eu nibh in neque sodales posuere fermentum sed nisl. Nulla imperdiet, sem vitae scelerisque sodales, ante ligula volutpat mauris, non volutpat turpis nunc quis mauris. Nulla facilisi. Donec vel suscipit quam, a varius lectus.",
                r.nextBoolean()
            )
        }.map { ModuleInstalledRvItem(it) }
        update(newItems)
    }
    val itemBinding = OnItemBind<ComparableRvItem<*>> { itemBinding, _, item ->
        item.bind(itemBinding)
        itemBinding.bindExtra(BR.viewModel, this@ModuleViewModel)
    }

    fun activityPressed(item: ModuleInstalledRvItem) {
        item.isActive.value = !item.isActive.value
    }

    fun reinstallPressed(item: ModuleInstalledRvItem) = Unit
    fun uninstallPressed(item: ModuleInstalledRvItem) = Unit

}