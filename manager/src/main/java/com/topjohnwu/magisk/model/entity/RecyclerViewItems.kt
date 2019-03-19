package com.topjohnwu.magisk.model.entity

import androidx.databinding.ObservableBoolean
import com.skoumal.teanity.databinding.ComparableRvItem
import com.skoumal.teanity.util.ComparableCallback
import com.skoumal.teanity.util.KObservableField
import com.topjohnwu.magisk.R

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