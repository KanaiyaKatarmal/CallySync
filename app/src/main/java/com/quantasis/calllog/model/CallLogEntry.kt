package com.quantasis.calllog.model

data class CallLogEntry(
    val name: String?,
    val number: String,
    val date: String,
    val duration: String,
    val type: String,
    val subStatus: String,
    val typeCode: Int
)

