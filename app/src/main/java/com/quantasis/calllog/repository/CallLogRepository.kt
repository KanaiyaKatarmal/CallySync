package com.quantasis.calllog.repository

import androidx.paging.PagingSource
import com.quantasis.calllog.database.CallLogDao
import com.quantasis.calllog.database.CallLogEntryEntity
import java.util.Date

class CallLogRepository(private val dao: CallLogDao) {
    fun getCallLogs(search: String?, startDate: Date?, endDate: Date?): PagingSource<Int, CallLogEntryEntity> {
        return dao.getCallLogsPaging(search, startDate, endDate)
    }
}