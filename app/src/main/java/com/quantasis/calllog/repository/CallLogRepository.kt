package com.quantasis.calllog.repository

import androidx.paging.PagingSource
import com.quantasis.calllog.database.CallLogDao
import com.quantasis.calllog.database.CallLogEntity
import java.util.Date

class CallLogRepository(private val dao: CallLogDao) {

    fun getCallLogs(search: String?, startDate: Date?, endDate: Date?,type: CallLogPageType): PagingSource<Int, CallLogEntity>  {
        return when (type) {
            CallLogPageType.ALL -> dao.getCallLogsPaging(search, startDate, endDate)
            CallLogPageType.INCOMING -> dao.getIncomingCallsPaging(search, startDate, endDate)
            CallLogPageType.OUTGOING -> dao.getOutgoingCallsPaging(search, startDate, endDate)
            CallLogPageType.MISSED -> dao.getMissedCallsPaging(search, startDate, endDate)
            CallLogPageType.REJECTED -> dao.getRejectedCallsPaging(search, startDate, endDate)
            CallLogPageType.UNANSWERED_OUTGOING -> dao.getLatestUnansweredOutgoingCallsPerNumberPaging(search, startDate, endDate)
            CallLogPageType.LATEST_UNRETURNED_MISSED -> dao.getLatestUnreturnedMissedCallsPerNumberPaging(search, startDate, endDate)
            CallLogPageType.UNKNOWN -> dao.getUnknownNumberCallsPaging(search, startDate, endDate)
            CallLogPageType.BLOCKED -> dao.getBlockedCallsPaging(search, startDate, endDate)
        }
    }

    suspend fun getCallSummary(startDate: Date?, endDate: Date?) =
        dao.getCallSummary(startDate, endDate)

    suspend fun getTop10Callers() = dao.getTop10Callers()
    suspend fun getTop10Incoming() = dao.getTop10Incoming()
    suspend fun getTop10Outgoing() = dao.getTop10Outgoing()
    suspend fun getTop10Duration() = dao.getTop10Duration()
    suspend fun getTop10IncomingDuration() = dao.getTop10IncomingDuration()
    suspend fun getTop10OutgoingDuration() = dao.getTop10OutgoingDuration()
}

enum class CallLogPageType {
    ALL,
    INCOMING,
    OUTGOING,
    MISSED,
    REJECTED,
    UNANSWERED_OUTGOING,
    LATEST_UNRETURNED_MISSED,
    UNKNOWN,
    BLOCKED
}