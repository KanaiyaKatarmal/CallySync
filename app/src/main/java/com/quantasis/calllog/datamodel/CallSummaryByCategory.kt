package com.quantasis.calllog.datamodel

data class CallSummaryByCategory(
    val label: String,
    val count: Int,
    val duration: Long
)