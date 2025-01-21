package com.chillguy.chatzapp.utils

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

object Utils {

    fun isPatternMatches(text: String, pattern: Pattern): Boolean {
       return pattern.matcher(text).matches()
    }

    fun getCurrentTimeStamp(): String {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val date = Date(System.currentTimeMillis())
            formatter.format(date)
        } catch (e: Exception) {
            ""
        }
    }

    fun getTimeWithoutSeconds(time: String): String {
        val dateArray = time.split(" ")
        if (dateArray.size > 1) {
            val timeArray = dateArray[1].split(":")
            if (timeArray.size > 1) {
                return "${timeArray[0]}:${timeArray[1]}"
            }
        }
        return time
    }

}