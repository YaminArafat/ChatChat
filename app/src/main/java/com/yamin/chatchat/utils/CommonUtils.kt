package com.yamin.chatchat.utils

import java.text.SimpleDateFormat
import java.util.*

class CommonUtils {
    companion object {
        const val TAG = "CommonUtils"

        fun getFormattedTime(timestamp: Long): String {
            val formatter = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
            return formatter.format(timestamp)
        }
    }
}