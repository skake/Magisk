package com.topjohnwu.magisk.model.zip

import com.topjohnwu.magisk.util.forEach
import com.topjohnwu.superuser.io.SuFile
import java.io.File
import java.util.zip.ZipInputStream


class Zip private constructor(private val values: Builder) {

    companion object {
        operator fun invoke(builder: Builder.() -> Unit): Zip {
            return Zip(Builder().apply(builder))
        }
    }

    class Builder {
        lateinit var zip: File
        lateinit var destination: File
        var path = ""
        var isPathTrashy = false
    }

    @Suppress("RedundantLambdaArrow")
    fun unzip() {
        ensureRequiredParams()

        values.zip.zipStream().use {
            it.forEach { e ->
                if (!e.name.startsWith(values.path) || e.isDirectory) {
                    // Ignore directories, only create files
                    return@forEach
                }

                val name = if (values.isPathTrashy) {
                    e.name.substring(e.name.lastIndexOf('/') + 1)
                } else {
                    e.name
                }

                val out = File(values.destination, name)
                    .ensureExists()
                    .outputStream()
                //.suOutputStream() //this doesn't seem to create files at all

                it.copyTo(out)
            }
        }
    }

    private fun ensureRequiredParams() {
        if (!values.zip.exists()) {
            throw RuntimeException("Zip file does not exist")
        }
    }

    private fun File.ensureExists() = if (!parentFile.exists() && !parentFile.mkdirs()) {
        SuFile(parentFile, name).apply { parentFile.mkdirs() }
    } else {
        this
    }

    private fun File.zipStream() = ZipInputStream(inputStream())

}
