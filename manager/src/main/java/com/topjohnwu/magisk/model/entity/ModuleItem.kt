package com.topjohnwu.magisk.model.entity

import com.skoumal.teanity.util.ComparableEntity


data class ModuleItem(
    val name: String,
    val version: String,
    val author: String,
    val description: String,
    val isActive: Boolean
) : ComparableEntity<ModuleItem> {
    override fun contentSameAs(other: ModuleItem): Boolean {
        return name == other.name && version == other.version && author == other.author && description == other.description && isActive == other.isActive
    }

    override fun sameAs(other: ModuleItem): Boolean = contentSameAs(other)
}