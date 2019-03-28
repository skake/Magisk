package com.topjohnwu.magisk.model.su


class WriteOnlyList(
    private val callback: (String) -> Unit
) : ArrayList<String>() {

    override val size: Int = 0
    override fun get(index: Int): String = ""

    override fun add(element: String): Boolean {
        callback(element)
        return true
    }
}
