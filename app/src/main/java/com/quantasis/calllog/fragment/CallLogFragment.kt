package com.quantasis.calllog.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.adapter.CallLogAdapter
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.repository.CallLogRepository
import com.quantasis.calllog.viewModel.CallLogViewModel
import kotlinx.coroutines.launch
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import kotlinx.coroutines.flow.collectLatest

class CallLogFragment : Fragment(R.layout.fragment_call_log) {

    private val viewModel: CallLogViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val dao = AppDatabase.getInstance(requireContext()).callLogDao()
                return CallLogViewModel(CallLogRepository(dao)) as T
            }
        }
    }

    private lateinit var adapter: CallLogAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CallLogAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val searchBox = view.findViewById<EditText>(R.id.searchEditText)
        searchBox.addTextChangedListener {
            viewModel.setSearch(it.toString())
        }

        lifecycleScope.launch {
            viewModel.callLogs.collectLatest {
                adapter.submitData(it)
            }
        }
    }
}