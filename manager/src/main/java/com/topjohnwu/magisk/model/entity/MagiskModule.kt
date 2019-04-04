package com.topjohnwu.magisk.model.entity

import android.os.Parcelable
import androidx.annotation.AnyThread
import androidx.room.Entity
import com.topjohnwu.magisk.Constants
import com.topjohnwu.magisk.data.database.base.su
import io.reactivex.Single
import kotlinx.android.parcel.Parcelize
import java.io.File

sealed class MagiskModule(
    val id: String,
    val name: String,
    val author: String,
    val version: String,
    val versionCode: String
) : Parcelable {

    @Entity(tableName = "repos")
    @Parcelize
    data class Repository(
        private val _id: String,
        private val _name: String,
        private val _author: String,
        private val _version: String,
        private val _versionCode: String,
        val lastUpdate: Long
    ) : MagiskModule(_id, _name, _author, _version, _versionCode)

    @Parcelize
    data class Module(
        private val _id: String,
        private val _name: String,
        private val _author: String,
        private val _version: String,
        private val _versionCode: String,
        val path: String
    ) : MagiskModule(_id, _name, _author, _version, _versionCode)

}

@AnyThread
fun File.toModule(): Single<MagiskModule.Module> {
    val path = "${Constants.MAGISK_PATH}/$name"
    return "dos2unix < $path/module.prop".su()
        .map { it.first().toModule(path) }
}

fun Map<String, String>.toModule(path: String): MagiskModule.Module {
    return MagiskModule.Module(
        _id = get("id").orEmpty(),
        _name = get("name").orEmpty(),
        _author = get("author").orEmpty(),
        _version = get("version").orEmpty(),
        _versionCode = get("versionCode").orEmpty(),
        path = path
    )
}
