package com.topjohnwu.magisk.data.database

import com.topjohnwu.magisk.data.database.base.*


class StringsDao : BaseDao() {

    override val table = MagiskDB.Table.STRINGS

    fun delete(key: String) = query<Delete> {
        condition { equals("key", key) }
    }.ignoreElement()

    fun put(key: String, value: String) = query<Insert> {
        values(key to value)
    }.ignoreElement()

    fun fetch(key: String, default: String = "") = query<Select> {
        fields("value")
        condition { equals("key", key) }
    }.map { it.firstOrNull()?.values?.firstOrNull() ?: default }

}
