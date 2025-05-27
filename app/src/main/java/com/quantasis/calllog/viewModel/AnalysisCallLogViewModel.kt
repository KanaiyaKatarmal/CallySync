package com.quantasis.calllog.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.datamodel.CallerDashboardData
import com.quantasis.calllog.repository.CallLogRepository
import com.quantasis.calllog.repository.CallerDashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class AnalysisViewModel(private val repo: CallerDashboardRepository) : ViewModel() {

    suspend fun getCallerStats(
        number: String,
        startDate: Date?,
        endDate: Date?
    ): List<CallerDashboardData> = withContext(Dispatchers.IO) {
        repo.getCallerStats(number, startDate, endDate)
    }
}

class AnalysisViewModelFactory(private val repo: CallerDashboardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalysisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnalysisViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}