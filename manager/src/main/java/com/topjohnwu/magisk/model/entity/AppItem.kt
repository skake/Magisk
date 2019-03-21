package com.topjohnwu.magisk.model.entity

import android.graphics.drawable.Drawable


data class AppItem(
    val name: String,
    val packageName: String,
    val icon: Drawable?,
    val shouldHide: Boolean
)