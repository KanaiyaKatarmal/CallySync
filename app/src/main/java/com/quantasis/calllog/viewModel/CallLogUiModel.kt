package com.quantasis.calllog.viewModel

import com.quantasis.calllog.database.CallLogEntity

sealed class CallLogUiModel {
    data class Item(val entry: CallLogEntity) : CallLogUiModel()
    data class DateSeparator(val date: String) : CallLogUiModel()
}