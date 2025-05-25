package com.quantasis.calllog.interfacecallback

import com.quantasis.calllog.database.CallLogEntryEntity

interface OnCallLogItemClickListener {
    fun onItemClick(entry: CallLogEntryEntity)
}