package com.topjohnwu.magisk.util

import kotlin.random.Random

val random = Random(System.currentTimeMillis())
val specialChars = arrayOf('!', '@', '#', '$', '%', '&', '?')

fun String.replaceRandomWithSpecial(): String {
    var random: Char
    do {
        random = random()
    } while (random == '.')
    return replace(random, specialChars.random())
}

fun StringBuilder.appendIf(condition: Boolean, builder: StringBuilder.() -> Unit) =
    if (condition) apply(builder) else this
