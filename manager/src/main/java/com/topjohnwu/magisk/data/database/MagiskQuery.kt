package com.topjohnwu.magisk.data.database


data class MagiskQuery(private val _query: String) {
    val query = "magisk --sqlite $_query"
}