package com.topjohnwu.magisk.model.entity

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import com.skoumal.teanity.databinding.ComparableRvItem
import com.skoumal.teanity.util.ComparableCallback
import com.skoumal.teanity.util.DiffObservableList
import com.skoumal.teanity.util.KObservableField
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.util.toggle

class LoadingRvItem(
    val failText: String,
    val failActionText: String,
    private val failAction: () -> Unit,
    isFailed: Boolean = false
) : ComparableRvItem<LoadingRvItem>() {

    override val layoutRes = R.layout.item_loading_more

    val failed = ObservableBoolean(isFailed)

    fun failActionClicked() = failAction()

    override fun itemSameAs(other: LoadingRvItem) = true

    override fun contentSameAs(other: LoadingRvItem) = true

    companion object : ComparableCallback<LoadingRvItem>()
}

class SupportRvItem(val item: SupportItem) : ComparableRvItem<SupportRvItem>() {

    override val layoutRes: Int = R.layout.item_support

    override fun contentSameAs(other: SupportRvItem): Boolean = item.contentSameAs(other.item)
    override fun itemSameAs(other: SupportRvItem): Boolean = item.sameAs(other.item)

    companion object : ComparableCallback<SupportRvItem>()

}

class ModuleInstalledRvItem(val item: ModuleItem) : ComparableRvItem<ModuleInstalledRvItem>() {

    override val layoutRes: Int = R.layout.item_module_installed

    val isActive = KObservableField(item.isActive)

    override fun contentSameAs(other: ModuleInstalledRvItem): Boolean = false
    override fun itemSameAs(other: ModuleInstalledRvItem): Boolean = false

}

class ModuleRvItem(val item: ModuleItem) : ComparableRvItem<ModuleRvItem>() {

    override val layoutRes: Int = R.layout.item_module

    override fun contentSameAs(other: ModuleRvItem): Boolean = false
    override fun itemSameAs(other: ModuleRvItem): Boolean = false

}

class SuperuserRequestRvItem(val item: RootRequestItem) :
    ComparableRvItem<SuperuserRequestRvItem>() {

    override val layoutRes: Int = R.layout.item_superuser_request

    val shouldDebug = KObservableField(item.debug)
    val shouldGrantRoot = KObservableField(item.grantRoot)
    val shouldNotify = KObservableField(item.notify)

    override fun contentSameAs(other: SuperuserRequestRvItem): Boolean = false
    override fun itemSameAs(other: SuperuserRequestRvItem): Boolean = false

}

class AppHideRvItem(val item: AppItem) : ComparableRvItem<AppHideRvItem>() {

    override val layoutRes: Int = R.layout.item_app_hide

    val shouldHide = KObservableField(item.shouldHide)

    override fun contentSameAs(other: AppHideRvItem): Boolean = false
    override fun itemSameAs(other: AppHideRvItem): Boolean = false

}

class LogPage : ComparableRvItem<LogPage>() {

    val items = DiffObservableList(ComparableRvItem.callback)

    override val layoutRes: Int = R.layout.page_log

    override fun contentSameAs(other: LogPage): Boolean = false
    override fun itemSameAs(other: LogPage): Boolean = false
}

class LogRvItem : ComparableRvItem<LogRvItem>() {
    override val layoutRes: Int = R.layout.item_log

    val items = ObservableArrayList<ComparableRvItem<*>>()
    val isExpanded = KObservableField(false)

    fun toggle() = isExpanded.toggle()

    override fun contentSameAs(other: LogRvItem): Boolean = false
    override fun itemSameAs(other: LogRvItem): Boolean = false
}

class LogLineRvItem : ComparableRvItem<LogLineRvItem>() {
    override val layoutRes: Int = R.layout.item_log_line

    override fun contentSameAs(other: LogLineRvItem): Boolean = false
    override fun itemSameAs(other: LogLineRvItem): Boolean = false
}

class MagiskPage : ComparableRvItem<LogPage>() {

    override val layoutRes: Int = R.layout.page_magisk

    override fun contentSameAs(other: LogPage): Boolean = false
    override fun itemSameAs(other: LogPage): Boolean = false
}