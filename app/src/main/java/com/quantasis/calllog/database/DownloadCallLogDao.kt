package com.quantasis.calllog.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quantasis.calllog.datamodel.CallSummaryByCategory
import com.quantasis.calllog.datamodel.CallerDashboardData
import com.quantasis.calllog.datamodel.TopCallListItemSummary
import com.quantasis.calllog.datamodel.TopCallerEntry
import com.quantasis.calllog.datamodel.TopDurationEntry
import java.util.Date

@Dao
interface DownloadCallLogDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCallLogs(callLogs: List<CallLogEntity>)

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
    ): List<CallLogEntity>


    @Query("""
        SELECT * FROM calllog 
        WHERE (:search IS NULL OR name LIKE '%' || :search || '%' OR number LIKE '%' || :search || '%') 
        AND (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        AND callType = 1
        ORDER BY date DESC
    """)
    fun getIncomingCallsPaging(
        search: String?,
        startDate: Date?,
        endDate: Date?
    ): List<CallLogEntity>


    @Query("""
        SELECT * FROM calllog 
        WHERE (:search IS NULL OR name LIKE '%' || :search || '%' OR number LIKE '%' || :search || '%') 
        AND (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        AND callType = 2
        ORDER BY date DESC
    """)
    fun getOutgoingCallsPaging(
        search: String?,
        startDate: Date?,
        endDate: Date?
    ): List<CallLogEntity>


    @Query("""
        SELECT * FROM calllog 
        WHERE (:search IS NULL OR name LIKE '%' || :search || '%' OR number LIKE '%' || :search || '%') 
        AND (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        AND callType = 3
        ORDER BY date DESC
    """)
    fun getMissedCallsPaging(
        search: String?,
        startDate: Date?,
        endDate: Date?
    ): List<CallLogEntity>


    @Query("""
        SELECT * FROM calllog 
        WHERE (:search IS NULL OR name LIKE '%' || :search || '%' OR number LIKE '%' || :search || '%') 
        AND (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        AND callType = 5
        ORDER BY date DESC
    """)
    fun getRejectedCallsPaging(
        search: String?,
        startDate: Date?,
        endDate: Date?
    ): List<CallLogEntity>


    @Query("""
    SELECT * FROM calllog AS call
    WHERE call.callType = 2
      AND call.duration = 0
      AND call.date = (
          SELECT MAX(inner_call.date)
          FROM calllog AS inner_call
          WHERE inner_call.callType = 2
            AND inner_call.duration = 0
            AND inner_call.number = call.number
      )
      AND NOT EXISTS (
          SELECT 1 FROM calllog AS response
          WHERE response.number = call.number
            AND response.date > call.date
            AND response.duration > 0
            AND response.callType IN (1, 2)
      )
           
      AND (:search IS NULL OR call.name LIKE '%' || :search || '%' OR call.number LIKE '%' || :search || '%') 
      AND (:startDate IS NULL OR call.date >= :startDate)
      AND (:endDate IS NULL OR call.date <= :endDate)
    GROUP BY call.number
    ORDER BY call.date DESC
""")
    fun getLatestUnansweredOutgoingCallsPerNumberPaging(
        search: String?,
        startDate: Date?,
        endDate: Date?
    ): List<CallLogEntity>

    // Not Attended Call after Miss Call
    @Query("""
SELECT * FROM calllog AS missed
WHERE missed.callType IN (3, 5) -- Missed or Rejected incoming calls
  AND NOT EXISTS (
    SELECT 1 FROM calllog AS callback
    WHERE callback.number = missed.number
      AND callback.date > missed.date
      AND callback.callType IN (1, 2) -- Incoming or outgoing
      AND callback.duration > 0       -- Only if answered
  )
  AND missed.date = (
    SELECT MAX(innerMissed.date) FROM calllog AS innerMissed
    WHERE innerMissed.callType IN (3, 5)
      AND innerMissed.number = missed.number
  )
  AND (:search IS NULL OR missed.name LIKE '%' || :search || '%' OR missed.number LIKE '%' || :search || '%') 
  AND (:startDate IS NULL OR missed.date >= :startDate)
  AND (:endDate IS NULL OR missed.date <= :endDate)
GROUP BY missed.number
ORDER BY missed.date DESC
""")
    fun getLatestUnreturnedMissedCallsPerNumberPaging(
        search: String?,
        startDate: Date?,
        endDate: Date?
    ): List<CallLogEntity>


    @Query("""
    SELECT * FROM calllog 
    WHERE name IS NULL
      AND (:search IS NULL OR number LIKE '%' || :search || '%') 
      AND (:startDate IS NULL OR date >= :startDate)
      AND (:endDate IS NULL OR date <= :endDate)
    ORDER BY date DESC
""")
    fun getUnknownNumberCallsPaging(
        search: String?,
        startDate: Date?,
        endDate: Date?
    ): List<CallLogEntity>



    @Query("""
        SELECT * FROM calllog 
        WHERE (:search IS NULL OR name LIKE '%' || :search || '%' OR number LIKE '%' || :search || '%') 
        AND (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        AND callType = 6
        ORDER BY date DESC
    """)
    fun getBlockedCallsPaging(
        search: String?,
        startDate: Date?,
        endDate: Date?
    ): List<CallLogEntity>


}
