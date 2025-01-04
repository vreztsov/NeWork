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
        val dateFormat = SimpleDateFormat("dd MM yyyy", Locale.ENGLISH)
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(source) ?: return ""
        val str = dateFormat.format(date)
        val range = 3..4
        return str.replaceRange(range, replaceMonth(str.substring(range)))
    }

    private fun replaceMonth(digits: String): String = when (digits) {
        "01" -> "января"
        "02" -> "февраля"
        "03" -> "марта"
        "04" -> "апреля"
        "05" -> "мая"
        "06" -> "июня"
        "07" -> "июля"
        "08" -> "августа"
        "09" -> "сентября"
        "10" -> "октября"
        "11" -> "ноября"
        "12" -> "декабря"
        else -> "???"
    }


}