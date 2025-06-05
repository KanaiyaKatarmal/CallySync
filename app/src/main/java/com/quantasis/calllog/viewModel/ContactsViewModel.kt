package com.quantasis.calllog.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.datamodel.ContactCallInfo
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    private val contactDao = AppDatabase.getInstance(application).contactDao()
    private val queryLiveData = MutableLiveData<String>("")

    val contactCallInfoList = MediatorLiveData<List<ContactCallInfo>>()

    init {
        contactCallInfoList.addSource(queryLiveData) { query ->
            viewModelScope.launch {
                contactDao.searchContactCallInfo(query).observeForever {
                    contactCallInfoList.value = it
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        queryLiveData.value = query
    }
}