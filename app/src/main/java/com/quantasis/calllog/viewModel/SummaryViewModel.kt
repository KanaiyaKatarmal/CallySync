package com.quantasis.calllog.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quantasis.calllog.datamodel.CallSummary
import com.quantasis.calllog.repository.CallLogRepository
import kotlinx.coroutines.launch
import java.util.Date

class SummaryViewModel(private val repository: CallLogRepository) : ViewModel() {
    private val _summaryData = MutableLiveData<List<CallSummary>>()
    val summaryData: LiveData<List<CallSummary>> = _summaryData

    fun loadSummary(startDate: Date?, endDate: Date?) {
        viewModelScope.launch {
            _summaryData.value = repository.getCallSummary(startDate, endDate)
        }
    }
}

class SummaryViewModelFactory(private val repository: CallLogRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SummaryViewModel::class.java)) {
            return SummaryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}