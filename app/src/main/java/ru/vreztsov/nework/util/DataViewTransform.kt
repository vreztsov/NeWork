package ru.vreztsov.nework.util

import android.annotation.SuppressLint
import android.util.Patterns
import ru.vreztsov.nework.dto.Job
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataViewTransform {

    fun dataToTextView(source: String): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
        val date = parseDateFromNeWorkString(source) ?: return ""
        return dateFormat.format(date)
    }

    fun checkLinkFormat(link: String?): Boolean =
        link?.let { Patterns.WEB_URL.matcher(it).matches() } ?: false

    fun jobDateToTextView(source: String): String {
        val date = parseDateFromNeWorkString(source) ?: return ""
        return dateToTextView(date)
    }

    fun periodToString(job: Job): String {
        val start = jobDateToTextView(job.start)
        val finish = job.finish?.let { jobDateToTextView(it) } ?: "НВ"
        return String.format("%s - %s", start, finish)
    }

    fun periodToString(start: Long, finish: Long?): String {
        val startString = dateToTextView(Date(start))
        val finishString = finish?.let { dateToTextView(Date(it)) } ?: "НВ"
        return String.format("%s - %s", startString, finishString)
    }

    @SuppressLint("SimpleDateFormat")
    fun parseDateFromNeWorkString(source: String): Date? = try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(source)
    } catch (e: ParseException) {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(source)
    }


    @SuppressLint("SimpleDateFormat")
    fun parseDateToNeWorkString(source: Date): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(source)

    private fun dateToTextView(date: Date): String {
        val dateFormat = SimpleDateFormat("dd MM yyyy", Locale.ENGLISH)
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