package ru.vreztsov.nework.util

import android.annotation.SuppressLint
import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.Locale

object DataViewTransform {

    @SuppressLint("SimpleDateFormat")
    fun dataToTextView(source: String): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(source) ?: return ""
        return dateFormat.format(date)
    }

    fun checkLinkFormat(link: String?): Boolean =
        link?.let { Patterns.WEB_URL.matcher(it).matches() } ?: false

    @SuppressLint("SimpleDateFormat")
    fun jobDataToTextView(source: String): String {
        val dateFormat = SimpleDateFormat("dd MMMMMMMMMMM yyyy", Locale.ENGLISH)
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(source) ?: return ""
        return dateFormat.format(date)
    }


}