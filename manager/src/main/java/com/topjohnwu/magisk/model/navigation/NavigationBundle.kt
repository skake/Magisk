package com.topjohnwu.magisk.model.navigation

import android.os.Bundle
import com.topjohnwu.magisk.util.directions.FlashAction


var Bundle.action: FlashAction?
    get() = getParcelable(ID.ACTION)
    set(value) = putParcelable(ID.ACTION, value)

var Bundle.data: String?
    get() = getString(ID.DATA)
    set(value) = putString(ID.DATA, value)

private object ID {
    const val ACTION = "action"
    const val DATA = "data"
}
