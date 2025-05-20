package com.quantasis.calllog.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.quantasis.calllog.model.CallLogEntry
import com.quantasis.calllog.repository.CallLogRepo
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.provider.CallLog

class CallLogViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val repository = CallLogRepo(application.applicationContext)

    private val _callLogs = MutableLiveData<List<CallLogEntry>>()
    val callLogs: LiveData<List<CallLogEntry>> = _callLogs

    private val _filteredLogs = MutableLiveData<List<CallLogEntry>>()
    val filteredLogs: LiveData<List<CallLogEntry>> = _filteredLogs

    fun loadCallLogs() {
        _isLoading.value = true
        viewModelScope.launch {
            val logs = withContext(Dispatchers.IO) {
                repository.getCallLogs()
            }
            _callLogs.value = logs
            _isLoading.value = false
        }
    }

    fun filterCallLogs(filter: String) {
        val all = _callLogs.value
        _filteredLogs.value = when (filter) {
            "Incoming Answered" -> all?.filter { it.typeCode == CallLog.Calls.INCOMING_TYPE && it.subStatus == "Answered" }
            "Incoming Missed" -> all?.filter { (it.typeCode == CallLog.Calls.INCOMING_TYPE && it.subStatus == "Missed") || (it.typeCode == CallLog.Calls.MISSED_TYPE || it.subStatus == "Missed") }
            "Outgoing Answered" -> all?.filter { it.typeCode == CallLog.Calls.OUTGOING_TYPE && it.subStatus == "Answered" }
            "Outgoing Unanswered" -> all?.filter { it.typeCode == CallLog.Calls.OUTGOING_TYPE && it.subStatus == "Unanswered" }
            "Rejected" -> all?.filter { it.typeCode == CallLog.Calls.REJECTED_TYPE || it.subStatus == "Reject" }
            else -> all
        }
    }

    fun searchLogs(query: String) {
        val all = _callLogs.value ?: return // Prevents null crash
        val filtered = if (query.isBlank()) {
            all
        } else {
            all.filter {
                it.name?.contains(query, ignoreCase = true) == true
            }
        }

        _filteredLogs.postValue(filtered)
    }

}