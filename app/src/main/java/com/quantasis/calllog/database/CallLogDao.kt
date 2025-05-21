package com.quantasis.calllog.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface CallLogDao {
    @Insert
    suspend fun insert(log: CallLogEntryEntity)

    @Query("""
        SELECT * FROM calllog 
        WHERE (:search IS NULL OR name LIKE '%' || :search || '%' OR number LIKE '%' || :search || '%') 
        AND (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        ORDER BY date DESC
    """)
    fun getCallLogsPaging(
        search: String?,
        startDate: Date?,
        endDate: Date?
    ): PagingSource<Int, CallLogEntryEntity>

    // All calls
    @Query("SELECT * FROM calllog ORDER BY date DESC")
    fun getAll(): LiveData<List<CallLogEntryEntity>>

    // Incoming
    @Query("SELECT * FROM calllog WHERE callType = 1 ORDER BY date DESC")
    fun getIncomingCalls(): LiveData<List<CallLogEntryEntity>>

    // Outgoing
    @Query("SELECT * FROM calllog WHERE callType = 2 ORDER BY date DESC")
    fun getOutgoingCalls(): LiveData<List<CallLogEntryEntity>>

    // Missed
    @Query("SELECT * FROM calllog WHERE callType = 3 ORDER BY date DESC")
    fun getMissedCalls(): LiveData<List<CallLogEntryEntity>>

    // Rejected
    @Query("SELECT * FROM calllog WHERE callType = 5 ORDER BY date DESC")
    fun getRejectedCalls(): LiveData<List<CallLogEntryEntity>>


    // Outgoing but not picked up (duration = 0)
    @Query("SELECT * FROM calllog WHERE callType = 2 AND duration = 0 ORDER BY date DESC")
    fun getUnansweredOutgoingCalls(): LiveData<List<CallLogEntryEntity>>



    // Not Attended Call after Miss Call
    @Query("""
    SELECT * FROM calllog AS missed
    WHERE missed.callType = 3
      AND NOT EXISTS (
        SELECT 1 FROM calllog AS callback
        WHERE callback.callType IN (1, 2)
          AND callback.number = missed.number
          AND callback.date > missed.date
          AND callback.duration > 0
      )
      AND missed.date = (
        SELECT MAX(innerMissed.date) FROM calllog AS innerMissed
        WHERE innerMissed.callType = 3
          AND innerMissed.number = missed.number
      )
    GROUP BY missed.number
    ORDER BY missed.date DESC
""")
    fun getLatestUnreturnedMissedCallsPerNumber(): LiveData<List<CallLogEntryEntity>>


    // Unknown numbers (where name is 'Unknown')
    @Query("SELECT * FROM calllog WHERE name = 'Unknown' ORDER BY date DESC")
    fun getUnknownNumberCalls(): LiveData<List<CallLogEntryEntity>>

    // Blocked
    @Query("SELECT * FROM calllog WHERE callType = 6 ORDER BY date DESC")
    fun getBlockedCalls(): LiveData<List<CallLogEntryEntity>>

}
