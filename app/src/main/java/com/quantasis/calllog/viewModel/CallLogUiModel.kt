package com.quantasis.calllog.viewModel

import com.quantasis.calllog.database.CallLogEntryEntity

sealed class CallLogUiModel {
    data class Item(val entry: CallLogEntryEntity) : CallLogUiModel()
    data class DateSeparator(val date: String) : CallLogUiModel()
}