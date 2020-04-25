package ru.skillbranch.skillarticles.extensions

import kotlinx.coroutines.yield


fun String?.indexesOf(query: String, ignoreCase: Boolean = true): List<Int> {
    if (query.isEmpty()) return listOf<Int>()
    val rexp = if (ignoreCase) Regex("$query", RegexOption.IGNORE_CASE) else  Regex("$query")
    val matches = rexp.findAll(this.toString(), 0)
    val indices = emptyList<Int>().toMutableList()
    matches.forEach { indices += it.range.first }
    return indices
}