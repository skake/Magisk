package com.topjohnwu.magisk.model.entity


data class RootRequestItem(
    val app: AppItem,
    val grantRoot: Boolean,
    val notify: Boolean,
    val debug: Boolean
)