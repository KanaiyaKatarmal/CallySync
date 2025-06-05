package com.quantasis.calllog.datamodel

data class ContactCallInfo(
    val contactId: String,
    val contactName: String?,
    val photoUri: String?,
    val phone: String,
    val callCount: Int
)