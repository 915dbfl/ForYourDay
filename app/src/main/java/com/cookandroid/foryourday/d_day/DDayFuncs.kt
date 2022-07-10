package com.cookandroid.foryourday.d_day

import java.text.SimpleDateFormat
import java.util.*

class DDayFuncs {
    fun getDDay(date: String): String{
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time
        val format = SimpleDateFormat("yyyy.M.dd", Locale.getDefault())
        val dDate = format.parse(date).time
        val count = (today-dDate) / (60 * 60 * 24 * 1000)
        return when {
            count.toInt() > 0 -> {
                "D + $count"
            }
            count.toInt() == 0 -> {
                "D - DAY"
            }
            else -> {
                "D - ${-count}"
            }
        }
    }
}