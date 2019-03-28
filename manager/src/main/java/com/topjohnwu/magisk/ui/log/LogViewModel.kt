package com.topjohnwu.magisk.ui.log

import androidx.databinding.ObservableArrayList
import com.skoumal.teanity.databinding.ComparableRvItem
import com.topjohnwu.magisk.BR
import com.topjohnwu.magisk.model.entity.LogLineRvItem
import com.topjohnwu.magisk.model.entity.LogPage
import com.topjohnwu.magisk.model.entity.LogRvItem
import com.topjohnwu.magisk.model.entity.MagiskPage
import com.topjohnwu.magisk.ui.base.MagiskViewModel
import com.topjohnwu.magisk.ui.events.ViewEvent
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.OnItemBind


class LogViewModel : MagiskViewModel(), BindingViewPagerAdapter.PageTitles<ComparableRvItem<*>> {

    val pages = ObservableArrayList<ComparableRvItem<*>>()
    val itemBinding = OnItemBind<ComparableRvItem<*>> { itemBinding, _, item ->
        item.bind(itemBinding)
        itemBinding.bindExtra(BR.viewModel, this@LogViewModel)
    }

    private val log get() = pages.filterIsInstance<LogPage>().first()
    private val magisk get() = pages.filterIsInstance<MagiskPage>().first()

    init {
        pages.add(LogPage())
        pages.add(MagiskPage())

        val logEntries = (0 until 5).map { LogLineRvItem() }
        val logLines = (0..10).map {
            LogRvItem().apply {
                items.addAll(logEntries)
            }
        }

        log.items.addAll(logLines)
    }

    override fun getPageTitle(position: Int, item: ComparableRvItem<*>?) = when (item) {
        is LogPage -> "Superuser"
        is MagiskPage -> "Magisk"
        else -> "N/A"
    }

    fun sheetBackPressed() = ViewEvent.BACK_PRESS.publish()

}
