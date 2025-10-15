package me.dirtydan16.betterjump.utils.extensions

import com.intellij.openapi.util.NlsSafe

fun @NlsSafe CharSequence.lastIndexOfOrNull(targetChar: Char, startSearchIndex: Int): Int? {
    val index = lastIndexOf(targetChar,startSearchIndex)
    if (index == -1) {
        return null
    }

    return index
}

fun @NlsSafe CharSequence.indexOfOrNull(targetChar: Char, startSearchIndex: Int): Int? {
    val index = indexOf(targetChar,startSearchIndex)
    if (index == -1) {
        return null
    }

    return index
}