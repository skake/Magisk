package com.topjohnwu.magisk.util

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


fun ZipInputStream.forEach(callback: (ZipEntry) -> Unit) {
    var entry: ZipEntry? = nextEntry
    while (entry != null) {
        callback(entry)
        entry = nextEntry
    }
}
