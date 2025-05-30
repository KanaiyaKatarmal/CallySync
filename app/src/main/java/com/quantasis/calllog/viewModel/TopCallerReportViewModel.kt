package com.quantasis.calllog.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quantasis.calllog.datamodel.TopCallListItemSummary
import com.quantasis.calllog.datamodel.StatType
import com.quantasis.calllog.repository.CallLogRepository
import kotlinx.coroutines.launch

class TopCallerReportViewModel(
    private val repository: CallLogRepository,
    private val type: StatType
) : ViewModel() {

    private val _data = MutableLiveData<List<TopCallListItemSummary>>()
    val data: LiveData<List<TopCallListItemSummary>> = _data

    init {
        viewModelScope.launch {
            val result = when (type) {
                StatType.TOP_10_CALLERS -> repository.getTop10Callers()
                StatType.TOP_10_INCOMING -> repository.getTop10Incoming()
                StatType.TOP_10_OUTGOING -> repository.getTop10Outgoing()
                StatType.TOP_10_DURATION -> repository.getTop10Duration()
                StatType.TOP_10_INCOMING_DURATION -> repository.getTop10IncomingDuration()
                StatType.TOP_10_OUTGOING_DURATION -> repository.getTop10OutgoingDuration()
                else -> {repository.getTop10Callers()}
            }
            _data.postValue(result)
        }
    }
}

class TopCallerReportViewModelFactory(
    private val repository: CallLogRepository,
    private val type: StatType
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TopCallerReportViewModel(repository, type) as T
    }
}

