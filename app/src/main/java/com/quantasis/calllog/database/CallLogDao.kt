package com.quantasis.calllog.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.quantasis.calllog.datamodel.CallSummary
import com.quantasis.calllog.datamodel.CallerDashboardData
import com.quantasis.calllog.datamodel.CallerSummary
import com.quantasis.calllog.datamodel.TopCallerEntry
import com.quantasis.calllog.datamodel.TopDurationEntry
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
    ): PagingSource<Int, CallLogEntryEntity>


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
    ): PagingSource<Int, CallLogEntryEntity>


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
    ): PagingSource<Int, CallLogEntryEntity>


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
    ): PagingSource<Int, CallLogEntryEntity>


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
      AND (:search IS NULL OR call.number LIKE '%' || :search || '%')
      AND (:startDate IS NULL OR call.date >= :startDate)
      AND (:endDate IS NULL OR call.date <= :endDate)
    GROUP BY call.number
    ORDER BY call.date DESC
""")
    fun getLatestUnansweredOutgoingCallsPerNumberPaging(
        search: String?,
        startDate: Date?,
        endDate: Date?
    ): PagingSource<Int, CallLogEntryEntity>

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
      AND (:search IS NULL OR missed.number LIKE '%' || :search || '%') 
      AND (:startDate IS NULL OR missed.date >= :startDate)
      AND (:endDate IS NULL OR missed.date <= :endDate)
    GROUP BY missed.number
    ORDER BY missed.date DESC
""")
    fun getLatestUnreturnedMissedCallsPerNumberPaging(
        search: String?,
        startDate: Date?,
        endDate: Date?
    ): PagingSource<Int, CallLogEntryEntity>


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
    ): PagingSource<Int, CallLogEntryEntity>



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
    ): PagingSource<Int, CallLogEntryEntity>


    @Query("""
    SELECT 
        CASE 
            WHEN callType = 2 AND duration = 0 THEN -1         -- Not Picked Outgoing
            ELSE callType
        END AS callCategory,
        COUNT(*) AS count,
        SUM(duration) AS totalDuration
    FROM calllog
    WHERE 
        REPLACE(REPLACE(REPLACE(number, '+', ''), ' ', ''), '-', '') LIKE '%' || REPLACE(REPLACE(REPLACE(:number, '+', ''), ' ', ''), '-', '') || '%'
        AND (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
    GROUP BY callCategory
""")
    suspend fun getCallerStats(
        number: String,
        startDate: Date?,
        endDate: Date?
    ): List<CallerDashboardData>


    @Query("""
    SELECT 'All Calls' AS label, COUNT(*) AS count, SUM(duration) AS duration 
    FROM calllog
    WHERE (:startDate IS NULL OR date >= :startDate) AND (:endDate IS NULL OR date <= :endDate)
    UNION ALL
    SELECT 'Incoming', COUNT(*), SUM(duration) FROM calllog WHERE callType = 1 AND (:startDate IS NULL OR date >= :startDate) AND (:endDate IS NULL OR date <= :endDate)
    UNION ALL
    SELECT 'Outgoing', COUNT(*), SUM(duration) FROM calllog WHERE callType = 2 AND (:startDate IS NULL OR date >= :startDate) AND (:endDate IS NULL OR date <= :endDate)
    UNION ALL
    SELECT 'Missed', COUNT(*), SUM(duration) FROM calllog WHERE callType = 3 AND (:startDate IS NULL OR date >= :startDate) AND (:endDate IS NULL OR date <= :endDate)
    UNION ALL
    SELECT 'Rejected', COUNT(*), SUM(duration) FROM calllog WHERE callType = 5 AND (:startDate IS NULL OR date >= :startDate) AND (:endDate IS NULL OR date <= :endDate)
    UNION ALL
    SELECT 'Unanswered Outgoing', COUNT(*) AS count, SUM(duration) AS duration FROM calllog AS call
    WHERE call.callType = 2 AND call.duration = 0 AND call.date = (
        SELECT MAX(inner_call.date)
        FROM calllog AS inner_call
        WHERE inner_call.callType = 2 AND inner_call.duration = 0 AND inner_call.number = call.number)
    AND NOT EXISTS (
        SELECT 1 FROM calllog AS response
        WHERE response.number = call.number AND response.date > call.date
        AND response.duration > 0 AND response.callType IN (1, 2)
    )
    AND (:startDate IS NULL OR call.date >= :startDate) AND (:endDate IS NULL OR call.date <= :endDate)
    UNION ALL
    SELECT 'Unreturned Missed', COUNT(*) AS count, SUM(duration) AS duration FROM calllog AS missed
    WHERE missed.callType = 3 AND NOT EXISTS (
        SELECT 1 FROM calllog AS callback
        WHERE callback.callType IN (1, 2) AND callback.number = missed.number AND callback.date > missed.date AND callback.duration > 0
    )
    AND missed.date = (
        SELECT MAX(innerMissed.date) FROM calllog AS innerMissed WHERE innerMissed.callType = 3 AND innerMissed.number = missed.number
    )
    AND (:startDate IS NULL OR missed.date >= :startDate) AND (:endDate IS NULL OR missed.date <= :endDate)
    UNION ALL
    SELECT 'Unknown Numbers', COUNT(*), SUM(duration) FROM calllog WHERE name IS NULL AND (:startDate IS NULL OR date >= :startDate) AND (:endDate IS NULL OR date <= :endDate)
    UNION ALL
    SELECT 'Blocked', COUNT(*), SUM(duration) FROM calllog WHERE callType = 6 AND (:startDate IS NULL OR date >= :startDate) AND (:endDate IS NULL OR date <= :endDate)
""")
    suspend fun getCallSummary(startDate: Date?, endDate: Date?): List<CallSummary>


    @Query("""
        SELECT * FROM CallLog
        WHERE duration = (
            SELECT MAX(duration) FROM CallLog 
            WHERE  (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        )
        LIMIT 1
    """)
    suspend fun getLongestCall(
        startDate: Date? = null,
        endDate: Date? = null
    ): CallLogEntryEntity?

    @Query("""
        SELECT number, name, SUM(duration) as totalDuration
        FROM CallLog
        WHERE  (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        GROUP BY number
        ORDER BY totalDuration DESC
        LIMIT 1
    """)
    suspend fun getTop1ByTotalDuration(
        startDate: Date? = null,
        endDate: Date? = null
    ): TopDurationEntry?

    @Query("""
    SELECT number, name, COUNT(*) as totalCalls
    FROM CallLog
    WHERE (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
    GROUP BY number
    ORDER BY totalCalls DESC
    LIMIT 1
""")
    suspend fun getTopCallerByTotalCalls(
        startDate: Date? = null,
        endDate: Date? = null
    ): TopCallerEntry?


    @Query("""
        SELECT name, number, COUNT(*) as total 
        FROM calllog 
        GROUP BY number 
        ORDER BY total DESC 
        LIMIT 10
    """)
    suspend fun getTop10Callers(): List<CallerSummary>

    @Query("""
        SELECT name, number, COUNT(*) as total 
        FROM calllog 
        WHERE callType = 1 
        GROUP BY number 
        ORDER BY total DESC 
        LIMIT 10
    """)
    suspend fun getTop10Incoming(): List<CallerSummary>

    @Query("""
        SELECT name, number, COUNT(*) as total 
        FROM calllog 
        WHERE callType = 2 
        GROUP BY number 
        ORDER BY total DESC 
        LIMIT 10
    """)
    suspend fun getTop10Outgoing(): List<CallerSummary>

    @Query("""
        SELECT name, number, SUM(duration) as total 
        FROM calllog 
        GROUP BY number 
        ORDER BY total DESC 
        LIMIT 10
    """)
    suspend fun getTop10Duration(): List<CallerSummary>

    @Query("""
        SELECT name, number, SUM(duration) as total 
        FROM calllog 
        WHERE callType = 1 
        GROUP BY number 
        ORDER BY total DESC 
        LIMIT 10
    """)
    suspend fun getTop10IncomingDuration(): List<CallerSummary>

    @Query("""
        SELECT name, number, SUM(duration) as total 
        FROM calllog 
        WHERE callType = 2 
        GROUP BY number 
        ORDER BY total DESC 
        LIMIT 10
    """)
    suspend fun getTop10OutgoingDuration(): List<CallerSummary>
}
