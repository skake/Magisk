package com.topjohnwu.magisk.data.database

import android.content.ContentValues
import android.content.Context
import androidx.annotation.AnyThread
import com.topjohnwu.superuser.Shell
import io.reactivex.Single
import timber.log.Timber


object MagiskDB {

    object Table {
        const val POLICY = "policies"
        const val LOG = "logs"
        const val SETTINGS = "settings"
        const val STRINGS = "strings"
    }

}

@AnyThread
fun MagiskQuery.query() = Single.just(Shell.su(query))
    .map { it.exec().out }
    .map { it.toMap() }


fun List<String>.toMap() = flatMap { it.split(Regex("\\|")) }
    .map { it.split("=", limit = 2) }
    .filter { it.size == 2 }
    .map { Pair(it[0], it[1]) }
    .toMap()