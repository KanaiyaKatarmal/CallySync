package com.quantasis.calllog.repository

import androidx.paging.PagingSource
import com.quantasis.calllog.database.CallLogDao
import com.quantasis.calllog.database.CallLogEntryEntity
import java.util.Date

class CallLogRepository(private val dao: CallLogDao) {

    fun getCallLogs(search: String?, startDate: Date?, endDate: Date?,type: CallLogPageType): PagingSource<Int, CallLogEntryEntity>  {
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