package com.quantasis.calllog.database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "calllog",
    indices = [Index(value = ["number", "date"], unique = true)])
data class CallLogEntity(
    val name: String?,
    val rawNumber: String,
    val countryCode: String?,
    val number: String,
    val date: Date,
    val duration: Int,
    val callType: Int,

    val note: String? = null,
    val tags: String? = null,

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)