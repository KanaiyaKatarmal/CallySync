package com.quantasis.calllog.repository

import com.quantasis.calllog.database.CallLogDao
import com.quantasis.calllog.datamodel.CallerDashboardData
import java.util.Date

class CallerDashboardRepository(private val dao: CallLogDao) {

    suspend fun getCallerStats(
        mobileNumber: String,
        startDate: Date?,
        endDate: Date?
    ): List<CallerDashboardData> {
        return dao.getCallerStats(mobileNumber, startDate, endDate)
    }
}