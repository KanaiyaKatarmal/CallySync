package com.quantasis.calllog.datamodel

data class TopCallerEntry(
    val number: String,
    val name: String?,
    val totalCalls: Int
)

data class TopDurationEntry(
    val number: String,
    val name: String?,
    val totalDuration: Int
)