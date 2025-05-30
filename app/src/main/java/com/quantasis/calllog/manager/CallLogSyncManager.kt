package com.quantasis.calllog.manager

import android.content.Context
import android.provider.CallLog
import android.telephony.TelephonyManager
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.database.CallLogEntity
import com.quantasis.calllog.util.PhoneNumberUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale

object CallLogSyncManager {

    private const val PREFS_NAME = "calllog_prefs"
    private const val KEY_LAST_SYNCED_DATE = "last_synced_date"

    private val syncMutex = Mutex()

    private fun getLastSyncedDate(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_LAST_SYNCED_DATE, 0L)
    }

    private fun setLastSyncedDate(context: Context, date: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_LAST_SYNCED_DATE, date).apply()
    }

    suspend fun syncCallLogsToRoom(context: Context, db: AppDatabase) {
        syncMutex.withLock {
            withContext(Dispatchers.IO) {
                val lastSynced = getLastSyncedDate(context)

                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val simCountry = telephonyManager.simCountryIso.uppercase(Locale.getDefault())

                val selection = if (lastSynced > 0) "${CallLog.Calls.DATE} > ?" else null
                val selectionArgs = if (lastSynced > 0) arrayOf(lastSynced.toString()) else null

                val resolver = context.contentResolver
                val cursor = resolver.query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    "${CallLog.Calls.DATE} ASC"
                )

                var latestSyncedDate = lastSynced

                cursor?.use {
                    val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
                    val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                    val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
                    val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
                    val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)

                    while (it.moveToNext()) {
                        val name = it.getString(nameIndex)
                        val number = it.getString(numberIndex)
                        val dateLong = it.getLong(dateIndex)
                        val date = Date(dateLong)
                        val duration = it.getInt(durationIndex)
                        val typeCode = it.getInt(typeIndex)

                        val result = PhoneNumberUtils.extractPhoneNumberParts(number,simCountry)

                        val entry = CallLogEntity(
                            name = name,
                            rawNumber = number,
                            countryCode = result.countryCode,
                            number = result.nationalNumber,
                            date = date,
                            duration = duration,
                            callType = typeCode
                        )

                        db.callLogDao().insert(entry)

                        if (dateLong > latestSyncedDate) {
                            latestSyncedDate = dateLong
                        }
                    }
                }

                if (latestSyncedDate > lastSynced) {
                    setLastSyncedDate(context, latestSyncedDate)
                }
            }
        }
    }
}