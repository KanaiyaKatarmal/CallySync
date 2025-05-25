package com.quantasis.calllog.datamodel

data class CallerDashboardData(
    val callCategory: Int,
    val count: Int,
    val totalDuration: Int
)