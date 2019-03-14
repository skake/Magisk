package com.topjohnwu.magisk.util

import android.view.View
import android.view.ViewTreeObserver


fun View.setOnViewReadyListener(callback: () -> Unit) =
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            callback()
        }
    })