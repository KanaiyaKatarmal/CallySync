package com.quantasis.calllog.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quantasis.calllog.datamodel.CallerDashboardData
import com.quantasis.calllog.repository.CallLogRepository
import com.quantasis.calllog.repository.CallerDashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class CallerDashboardViewModel(private val repository: CallerDashboardRepository) : ViewModel() {

    private val _dashboardData = MutableLiveData<List<CallerDashboardData>>()
    val dashboardData: LiveData<List<CallerDashboardData>> = _dashboardData

    fun loadDashboardData(mobileNumber: String, startDate: Date?, endDate: Date?) {
        viewModelScope.launch(Dispatchers.IO) {
                       val data = repository.getCallerStats(mobileNumber, startDate, endDate)

            // Compute totals
            val totalCount = data.sumOf { it.count }
            val totalDuration = data.sumOf { it.totalDuration }

            val result = data.toMutableList()
            result.add(CallerDashboardData(999, totalCount, totalDuration))

            _dashboardData.postValue(result)
        }
    }
}