package com.quantasis.calllog.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CallLogFragment : Fragment(R.layout.fragment_call_log) {

    private lateinit var adapter: CallLogAdapter
    private var startDate: Date? = null
    private var endDate: Date? = null

    private val viewModel: CallLogViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val dao = AppDatabase.getInstance(requireContext()).callLogDao()
                return CallLogViewModel(CallLogRepository(dao)) as T
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchBox = view.findViewById<EditText>(R.id.searchEditText)
        val startDateBtn = view.findViewById<Button>(R.id.startDateButton)
        val endDateBtn = view.findViewById<Button>(R.id.endDateButton)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        adapter = CallLogAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Search box input
        searchBox.addTextChangedListener {
            viewModel.setSearch(it.toString())
        }

        // Start Date Picker
        startDateBtn.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                startDateBtn.text = formatDate(date)
                viewModel.setDateRange(startDate, endDate)
            }
        }

        // End Date Picker
        endDateBtn.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                endDateBtn.text = formatDate(date)
                viewModel.setDateRange(startDate, endDate)
            }
        }

        // Observe paged data
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.callLogs.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day, 0, 0, 0)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(date)
    }
}