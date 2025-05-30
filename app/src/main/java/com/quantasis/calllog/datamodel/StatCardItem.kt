package com.quantasis.calllog.datamodel
data class StatCardItem(
    val title: String,
    val value: String,
    val statType: StatType,
    val number: String?,
)

enum class StatType {
    LONGEST_CALL,
    TOP_TOTAL_CALLS,
    TOP_10_CALLERS,
    TOP_10_INCOMING,
    TOP_10_OUTGOING,
    TOP_10_DURATION,
    TOP_10_INCOMING_DURATION,
    TOP_10_OUTGOING_DURATION,
    HIGHEST_TOTAL_CALL_DURATION
}