package com.quantasis.calllog.repository

import com.quantasis.calllog.database.CallLogDao
import com.quantasis.calllog.database.CallLogEntryEntity
import com.quantasis.calllog.datamodel.CallerDashboardData
import com.quantasis.calllog.datamodel.TopCallerEntry
import com.quantasis.calllog.datamodel.TopDurationEntry
import java.util.Date

class CallerDashboardRepository(private val dao: CallLogDao) {

    suspend fun getCallerStats(
        mobileNumber: String,
        startDate: Date?,
        endDate: Date?
    ): List<CallerDashboardData> {
        return dao.getCallerStats(mobileNumber, startDate, endDate)
    }

    suspend fun getLongestCall(
        startDate: Date?,
        endDate: Date?
    ): CallLogEntryEntity? {
        return dao.getLongestCall(startDate, endDate)
    }
    suspend fun getTopCallerByTotalCalls(
        startDate: Date?,
        endDate: Date?
    ): TopCallerEntry? {
        return dao.getTopCallerByTotalCalls(startDate, endDate)
    }


    suspend fun getHighestCallTotalDuration(
        startDate: Date?,
        endDate: Date?
    ): TopDurationEntry? {
        return dao.getTop1ByTotalDuration(startDate, endDate)
    }
}