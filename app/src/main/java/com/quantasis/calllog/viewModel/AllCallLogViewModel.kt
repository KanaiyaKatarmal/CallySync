package com.quantasis.calllog.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.database.CallLogEntryEntity

class AllCallLogViewModel(application: Application) : AndroidViewModel(application) {

    private val callLogDao = AppDatabase.getInstance(application)?.callLogDao()

    val allCallLogs: LiveData<List<CallLogEntryEntity>>? = callLogDao?.getAll()

}