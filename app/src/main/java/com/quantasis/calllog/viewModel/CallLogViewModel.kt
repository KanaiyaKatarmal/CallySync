package com.quantasis.calllog.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.quantasis.calllog.repository.CallLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CallLogViewModel(private val repository: CallLogRepository) : ViewModel() {

    private val searchQuery = MutableStateFlow<String?>(null)
    private val startDate = MutableStateFlow<Date?>(null)
    private val endDate = MutableStateFlow<Date?>(null)

    val callLogs: Flow<PagingData<CallLogUiModel>> = combine(
        searchQuery, startDate, endDate
    ) { query, start, end ->
        Triple(query, start, end)
    }.flatMapLatest { (query, start, end) ->
        Pager(PagingConfig(pageSize = 20)) {
            repository.getCallLogs(query, start, end)
        }.flow
            .map { pagingData ->
                pagingData.map { CallLogUiModel.Item(it) }.insertDateSeparators()
            }
    }.cachedIn(viewModelScope)

    fun setSearch(query: String?) {
        searchQuery.value = query
    }

    fun setDateRange(start: Date?, end: Date?) {
        startDate.value = start
        endDate.value = end
    }

    fun PagingData<CallLogUiModel.Item>.insertDateSeparators(): PagingData<CallLogUiModel> {
        return this.insertSeparators { before, after ->
            val beforeDate = before?.entry?.date
            val afterDate = after?.entry?.date

            if (afterDate != null && (beforeDate == null || !isSameDay(beforeDate, afterDate))) {
                CallLogUiModel.DateSeparator(formatDate(afterDate))
            } else null
        }
    }

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return fmt.format(date1) == fmt.format(date2)
    }

    fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(date)
    }
}