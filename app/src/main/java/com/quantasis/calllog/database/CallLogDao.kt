package com.quantasis.calllog.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CallLogDao {
    @Insert
    suspend fun insert(log: CallLogEntryEntity)

    @Query("SELECT * FROM calllog ORDER BY date DESC")
    suspend fun getAll(): List<CallLogEntryEntity>

}
