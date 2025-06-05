package com.quantasis.calllog.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.datamodel.ContactCallInfo

class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    private val contactDao = AppDatabase.getInstance(application).contactDao()

    val contactCallInfoList: LiveData<List<ContactCallInfo>> = contactDao.getContactCallInfoList()
}