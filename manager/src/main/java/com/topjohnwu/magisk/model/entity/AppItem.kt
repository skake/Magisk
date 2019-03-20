package com.topjohnwu.magisk.model.entity


data class AppItem(
    val name: String,
    val packageName: String,
    val icon: String,
    val shouldHide: Boolean
)