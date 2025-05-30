package com.quantasis.calllog.datamodel

data class TopCallListItemSummary(
    val name: String?,
    val number: String,
    val total: Int // call count or duration
)