package com.quantasis.calllog.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quantasis.calllog.datamodel.StatCardItem
import com.quantasis.calllog.datamodel.StatType
import com.quantasis.calllog.repository.CallerDashboardRepository
import com.quantasis.calllog.util.CallConvertUtil
import kotlinx.coroutines.launch
import java.util.Date

class AnalysisDetailedViewModel(application: Application,private val repository: CallerDashboardRepository) : AndroidViewModel(application) {

    private val _statistics = MutableLiveData<List<StatCardItem>>()
    val statistics: LiveData<List<StatCardItem>> = _statistics

    fun loadData(startDate: Date?, endDate: Date?) {
        viewModelScope.launch {
            val longestCall = repository.getLongestCall(startDate, endDate)
            val topCaller = repository.getTopCallerByTotalCalls(startDate, endDate)
            val highestCallerDuration = repository.getHighestCallTotalDuration(startDate, endDate)
            val top10Callers = StatCardItem("Top 10 Callers", "Tap to View", StatType.TOP_10_CALLERS)
            val top10Incoming = StatCardItem("Top 10 Incoming", "Tap to View", StatType.TOP_10_INCOMING)
            val top10Outgoing = StatCardItem("Top 10 Outgoing", "Tap to View", StatType.TOP_10_OUTGOING)
            val top10Duration = StatCardItem("Top 10 by Duration", "Tap to View", StatType.TOP_10_DURATION)
            val top10IncomingDuration = StatCardItem("Top 10 Incoming Dur.", "Tap to View", StatType.TOP_10_INCOMING_DURATION)
            val top10OutgoingDuration = StatCardItem("Top 10 Outgoing Dur.", "Tap to View", StatType.TOP_10_OUTGOING_DURATION)

            _statistics.postValue(
                listOf(
                    StatCardItem("Longest Call", "${longestCall?.name ?: "Unknown"}\n${CallConvertUtil.formatDuration(longestCall?.duration ?: 0)}", StatType.LONGEST_CALL),
                    StatCardItem("Top Caller (Total)", "${topCaller?.name ?: "Unknown"}\n${topCaller?.totalCalls ?: 0} calls", StatType.TOP_TOTAL_CALLS),
                    top10Callers,
                    top10Incoming,
                    top10Outgoing,
                    top10Duration,
                    top10IncomingDuration,
                    top10OutgoingDuration,
                    StatCardItem("Highest Total Call Duration", "${highestCallerDuration?.name ?: "Unknown"}\n${CallConvertUtil.formatDuration(highestCallerDuration?.totalDuration ?: 0)}", StatType.HIGHEST_TOTAL_CALL_DURATION),
                )
            )
        }
    }
}

class AnalysisDetailedViewModelFactory(
    private val application: Application,
    private val repository: CallerDashboardRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalysisDetailedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnalysisDetailedViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}