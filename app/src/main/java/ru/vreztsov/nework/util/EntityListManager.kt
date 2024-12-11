package ru.vreztsov.nework.util

val emptyStringsList = listOf("", "[]")

object EntityListManager {
    fun dtoListToString(list: List<Long>?): String = list?.toString() ?: ""

    fun stringToDtoList(data: String?): List<Long> = data
        ?.let { string ->
            if (emptyStringsList.contains(string)) return emptyList()
            val substr = data.substring(1, data.length - 1)
            substr.split(", ").map { it.toLong() }
        } ?: emptyList()
}