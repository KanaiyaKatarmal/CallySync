package com.quantasis.calllog.repository

import android.content.Context
import android.provider.CallLog
import android.text.format.DateFormat
import android.util.Log
import com.quantasis.calllog.model.CallLogEntry
import java.util.*


class CallLogRepo(private val context: Context) {

    fun getCallLogs(): List<CallLogEntry> {
        val list = mutableListOf<CallLogEntry>()
        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null,
            "${CallLog.Calls.DATE} DESC"
        ) ?: return list

        cursor.use {
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val accountIDIndex = it.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID)

            while (it.moveToNext()) {
                val number = it.getString(numberIndex)
                val name = it.getString(nameIndex)
                val date = Date(it.getLong(dateIndex))
                val duration = it.getString(durationIndex)
                val typeCode = it.getInt(typeIndex)
                val accountID = it.getInt(accountIDIndex)

                val type = when (typeCode) {
                    CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    CallLog.Calls.REJECTED_TYPE -> "Rejected"
                    else -> "Unknown"
                }

                val subStatus = when(typeCode) {
                    CallLog.Calls.INCOMING_TYPE ->"Answered"
                    CallLog.Calls.OUTGOING_TYPE -> {
                        if (duration.toInt() > 0) "Answered" else "Unanswered"
                    }
                    CallLog.Calls.REJECTED_TYPE -> "Rejected"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    CallLog.Calls.BLOCKED_TYPE -> "Blocked"
                    else -> "Unknown"
                }

                val dateFormatted = DateFormat.format("dd MMM yyyy, hh:mm a", date).toString()
                list.add(CallLogEntry(name, number, dateFormatted, duration, type, subStatus, typeCode))
            }
        }

        return list
    }
}
