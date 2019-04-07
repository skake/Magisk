package com.topjohnwu.magisk.ui.superuser

import android.content.pm.PackageManager
import com.skoumal.teanity.databinding.ComparableRvItem
import com.skoumal.teanity.util.DiffObservableList
import com.skoumal.teanity.util.KObservableField
import com.topjohnwu.magisk.BR
import com.topjohnwu.magisk.model.entity.AppHideRvItem
import com.topjohnwu.magisk.model.entity.AppItem
import com.topjohnwu.magisk.model.entity.RootRequestItem
import com.topjohnwu.magisk.model.entity.SuperuserRequestRvItem
import com.topjohnwu.magisk.model.observer.Observer
import com.topjohnwu.magisk.ui.base.MagiskViewModel
import com.topjohnwu.magisk.ui.events.ViewEvent
import com.topjohnwu.magisk.util.assign
import com.topjohnwu.magisk.util.findAppLabel
import io.reactivex.Single
import me.tatarka.bindingcollectionadapter2.OnItemBind
import kotlin.random.Random


class SuperuserViewModel(
    private val packageManager: PackageManager
) : MagiskViewModel() {

    val isIdling = KObservableField(true)
    val items = DiffObservableList(ComparableRvItem.callback).apply {
        val r = Random(System.currentTimeMillis())
        val newItems = (0..10).map {
            RootRequestItem(
                AppItem(
                    "Magisk Manager",
                    "com.topjohnwu.magisk",
                    null,
                    r.nextBoolean()
                ),
                r.nextBoolean(),
                r.nextBoolean(),
                r.nextBoolean()
            )
        }.map { SuperuserRequestRvItem(it) }
        update(newItems)
    }
    val itemsApps = DiffObservableList(ComparableRvItem.callback)
    val itemBinding = OnItemBind<ComparableRvItem<*>> { itemBinding, _, item ->
        item.bind(itemBinding)
        itemBinding.bindExtra(BR.viewModel, this@SuperuserViewModel)
    }

    private val appsState = KObservableField(State.LOADING)
    val appsLoaded = Observer(appsState) { appsState.value == State.LOADED }
    val appsLoadingFailed = Observer(appsState) { appsState.value == State.LOADING_FAILED }
    val appsLoading = Observer(appsState) { appsState.value == State.LOADING }

    init {
        Single.fromCallable { packageManager.getInstalledApplications(0) }
            .flattenAsFlowable { it }
            .filter { it.enabled }
            .map {
                AppItem(
                    it.findAppLabel(packageManager),
                    it.packageName.orEmpty(),
                    it.loadIcon(packageManager),
                    false //fixme this should be fetched
                )
            }
            .map { AppHideRvItem(it) }
            .toList()
            .map { it.sortedBy { it.item.name } }
            .doOnSubscribe { appsState.value = State.LOADING }
            .doOnSuccess { appsState.value = State.LOADED }
            .doOnError { appsState.value = State.LOADING_FAILED }
            .assign {
                onSuccess { itemsApps.update(it) }
            }
    }


    fun sheetBackPressed() = ViewEvent.BACK_PRESS.publish()

    fun hidePressed(item: AppHideRvItem) {
        item.shouldHide.value = !item.shouldHide.value
    }

    fun notifyPressed(item: SuperuserRequestRvItem) {
        item.shouldNotify.value = !item.shouldNotify.value
    }

    fun debugPressed(item: SuperuserRequestRvItem) {
        item.shouldDebug.value = !item.shouldDebug.value
    }

    fun removePressed(item: SuperuserRequestRvItem) {
        items.remove(item)
    }

    fun requestPressed(item: SuperuserRequestRvItem) {
        item.shouldGrantRoot.value = !item.shouldGrantRoot.value
    }

}
