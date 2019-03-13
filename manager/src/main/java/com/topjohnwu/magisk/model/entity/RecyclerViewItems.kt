package com.topjohnwu.magisk.model.entity

import androidx.databinding.ObservableBoolean
import com.skoumal.teanity.databinding.ComparableRvItem
import com.skoumal.teanity.util.ComparableCallback
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