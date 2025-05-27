package com.quantasis.calllog.datamodel

data class CallSummary(
    val label: String,
    val count: Int,
    val duration: Long
)