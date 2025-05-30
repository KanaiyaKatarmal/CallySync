package com.quantasis.calllog.database
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "calllog")
data class CallLogEntity(
    val name: String?,
    val rawNumber: String,
    val countryCode: String?,
    val number: String,
    val date: Date,
    val duration: Int,
    val callType: Int,


    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)