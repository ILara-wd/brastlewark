package com.warriorsdev.brastlewark.utils

import java.util.Locale

private const val SEPARATOR = "|"

/**
 * Checks if the text contains the "http" protocol and replaces it with https.
 *
 * If the string is not an URL or it contains https already, no change is made.
 */
fun String.asSafeURL() = if (contains("http") && !contains("https")) {
    replace("http", "https")
} else {
    this
}

/**
 * Returns a String from a List of strings by the defined [SEPARATOR].
 */
fun List<String>.asString() = StringBuilder().also { builder ->
    forEach {
        builder.append(it)
        builder.append(SEPARATOR)
    }
}.toString()

/**
 * Returns a String as a [List] of Strings splitting with the given [SEPARATOR].
 */
fun String.asList() = this.split(SEPARATOR).toList().filter { it.isNotEmpty() }.sortedBy { it }

/**
 * Returns the file name of a given URL.
 */
fun String.getFileNameFromURL() = split("/").last()

/**
 * Removes trash characters from a List of String to String Kotlin parsing, characters such as [ , ].
 */
fun List<String>.asCleanString() = this.map { it.replace(" T", "T") }.sortedBy { it }
    .toString()
    .replace("[", "")
    .replace(", ]", "")
    .replace("]", "")

/**
 * Retrieves the two letters conforming a name as capital letters.
 */
fun String.getNameCapitalLetters() = if (length > 2 && contains(" ")) {
    val words = this.split(" ")
    "${words.first().first()}${words.last().first()}".toUpperCase(Locale.getDefault())
} else if (length < 2) {
    this
} else {
    this.take(2)
}