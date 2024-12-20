package ru.vreztsov.nework.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.vreztsov.nework.dto.UserPreview
import java.lang.reflect.Type

val emptyStringsList = listOf("", "[]")

object EntityCollectionManager {

    private val gson = Gson()
    private val mapType: Type = object : TypeToken<Map<String, UserPreview>>() {}.type

    fun dtoListToString(list: List<Long>): String = list.toString()

    fun stringToDtoList(data: String): List<Long> = data
        .let { string ->
            if (emptyStringsList.contains(string)) return emptyList()
            val substr = data.substring(1, data.length - 1)
            substr.split(",").map { it.trim().toLong() }
        }

    fun dtoMapToString(map: Map<String, UserPreview>): String {
        return gson.toJson(map)
    }

    fun stringToDtoUserPreviewMap(data: String): Map<String, UserPreview> = try {
        gson.fromJson(data, mapType)
    } catch (e: Exception) {
        mutableMapOf()
    }

}