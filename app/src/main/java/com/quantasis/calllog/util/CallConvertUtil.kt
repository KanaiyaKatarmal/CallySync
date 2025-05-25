package com.quantasis.calllog.util

import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CallConvertUtil {

    fun callTypeToString(callType: Int): String {
        return when (callType) {
            1 -> "Incoming"
            2 -> "Outgoing"
            3 -> "Missed"
            4 -> "Voicemail"
            5 -> "Rejected"
            6 -> "Blocked"
            7 -> "Answered Externally"
            -1 -> "Not Picked Up"
            999 -> "Total"
            else -> "Unknown"
        }
    }

    fun formatDate(date: Date?): String {
        return if (date != null) {
            val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            sdf.format(date)
        } else {
            ""
        }
    }



    fun formatDuration(durationSeconds: Int): String {
        val hours = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        val seconds = durationSeconds % 60

        return "${hours}h ${minutes}m ${seconds}s"
    }
    private val categoryColorMap = mapOf(
        1 to Color.parseColor("#4CAF50"),   // Green
        2 to Color.parseColor("#2196F3"),   // Blue
        3 to Color.parseColor("#F44336"),     // Red
        4 to Color.parseColor("#795548"),     // Brown
        5 to Color.parseColor("#9E9E9E"),   // Gray
        6 to Color.parseColor("#673AB7"),    // Deep Purple
        7 to Color.parseColor("#00BCD4"),    // Cyan
        -1 to Color.parseColor("#FF9800"), // Orange
        999 to Color.parseColor("#00000000") // Orange
    )

    fun getColor(category: Int): Int {
        return categoryColorMap[category] ?: Color.LTGRAY
    }
}