package com.wildan.favoritemodule.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateFormat {

    private fun formatDate(date: String, format: String): String {
        var result = ""
        val timeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        try {
            val timeDate = timeFormat.parse(date)
            val newFormat = SimpleDateFormat(format, Locale.getDefault())

            result = newFormat.format(timeDate?: 0)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return result
    }

    fun getLongDate(date: String): String {
        return formatDate(date, "EEE, dd MMM yyyy")
    }
}