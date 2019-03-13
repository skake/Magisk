package com.topjohnwu.magisk.ui.home

import com.skoumal.teanity.databinding.ComparableRvItem
import com.skoumal.teanity.util.DiffObservableList
import com.skoumal.teanity.viewmodel.LoadingViewModel
import me.tatarka.bindingcollectionadapter2.OnItemBind
import com.topjohnwu.magisk.BR

class HomeViewModel : LoadingViewModel() {

    val items = DiffObservableList(ComparableRvItem.callback)
    val itemBinding = OnItemBind<ComparableRvItem<*>> { itemBinding, _, item ->
        item.bind(itemBinding)
        itemBinding.bindExtra(BR.viewModel, this@HomeViewModel)
    }

    fun retryLoadingButtonClicked() = Unit
    fun loadMoreItems() = Unit
}