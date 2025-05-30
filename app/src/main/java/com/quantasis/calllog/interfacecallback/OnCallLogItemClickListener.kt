package com.quantasis.calllog.interfacecallback

import com.quantasis.calllog.database.CallLogEntity

interface OnCallLogItemClickListener {
    fun onItemClick(entry: CallLogEntity)
}