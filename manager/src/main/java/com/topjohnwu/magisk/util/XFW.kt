package com.topjohnwu.magisk.util

import com.skoumal.teanity.util.KObservableField

fun KObservableField<Boolean>.toggle() {
    value = !value
}