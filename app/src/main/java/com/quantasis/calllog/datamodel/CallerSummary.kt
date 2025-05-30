package com.quantasis.calllog.datamodel

data class CallerSummary(
    val name: String?,
    val number: String,
    val total: Int // call count or duration
)