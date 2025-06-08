package com.quantasis.calllog.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.adapter.CallLogListAdapter
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.repository.CallLogRepository
import com.quantasis.calllog.viewModel.CallLogViewModel
import kotlinx.coroutines.launch
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.quantasis.calllog.database.CallLogEntity
import com.quantasis.calllog.interfacecallback.OnCallLogItemClickListener
import com.quantasis.calllog.repository.CallLogPageType
import com.quantasis.calllog.ui.CallerDashboardActivity
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CallLogListFragment : Fragment(R.layout.fragment_call_log) {

    companion object {
        private const val ARG_CALL_TYPE = "arg_call_type"

        private const val ARG_CALL_NUMBER = "arg_call_number"

        private const val ARG_CALL_START_DATE = "arg_call_startdate"

        private const val ARG_CALL_END_DATE = "arg_call_enddate"

        fun newInstance(type: CallLogPageType,number: String?=null,startDate: Date? =null,endDate: Date?=null): CallLogListFragment {
            return CallLogListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CALL_TYPE, type)
                    putString(ARG_CALL_NUMBER, number)
                    putSerializable(ARG_CALL_START_DATE, startDate)
                    putSerializable(ARG_CALL_END_DATE, endDate)
                }
            }
        }
    }

    private lateinit var adapter: CallLogListAdapter
    private var startDate: Date? = null
    private var endDate: Date? = null
    private var number: String? = null;


    private val viewModel: CallLogViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                val args = requireArguments()
                val callType = args.getSerializable(ARG_CALL_TYPE) as CallLogPageType
                number = args.getString(ARG_CALL_NUMBER)
                startDate = args.getSerializable(ARG_CALL_START_DATE) as? Date
                endDate = args.getSerializable(ARG_CALL_END_DATE) as? Date

                val dao = AppDatabase.getInstance(requireContext()).callLogDao()
                val repo = CallLogRepository(dao)
                return CallLogViewModel(repo, callType, number, startDate, endDate) as T
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
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val filterButton = view.findViewById<ImageButton>(R.id.filterButton)
        val menuButton = view.findViewById<ImageButton>(R.id.menuButton)

        val args = requireArguments()
        number = args.getString(ARG_CALL_NUMBER)
        searchBox.setText(number)

        adapter = CallLogListAdapter(object : OnCallLogItemClickListener {
            override fun onItemClick(entry: CallLogEntity) {
                val intent = Intent(requireContext(), CallerDashboardActivity::class.java).apply {
                    putExtra("name", entry.name)
                    putExtra("number", entry.number)
                    putExtra("startDate", startDate?.time ?: -1L)
                    putExtra("endDate", endDate?.time ?: -1L)
                }
                startActivity(intent)
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Search box input
        searchBox.addTextChangedListener {
            viewModel.setSearch(it.toString())
        }



        // Filter icon click
        filterButton.setOnClickListener {
            // TODO: Implement your filter action here

            DateRangeDialogFragment { start, end ->
                if (start == null || end == null) {
                    Toast.makeText(context, "Filter Cleared", Toast.LENGTH_SHORT).show()
                    //dateRangeText.text = "No Date ange Selected"
                } else {
                    //Toast.makeText(context, "Selected: $start to $end", Toast.LENGTH_SHORT).show()
                    //dateRangeText.text = "${dateFormat.format(start)} - ${dateFormat.format(end)}"
                }
                startDate=start;
                endDate=end
                viewModel.setDateRange(start, end)
            }.show(parentFragmentManager, "DateRangeDialog")
        }

        // 3-dot menu click showing popup
        menuButton.setOnClickListener {
            val popupMenu = android.widget.PopupMenu(requireContext(), menuButton)
            popupMenu.menuInflater.inflate(R.menu.call_log_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_save_pdf -> {
                        onSavePdfClicked()
                        true
                    }
                    R.id.action_save_csv -> {
                        onSaveCsvClicked()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        // Observe paged data
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.callLogs.collectLatest {
                adapter.submitData(it)
            }
        }
    }



    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    private fun onSavePdfClicked() {
        // TODO: Add your logic to save call log as PDF here
        Toast.makeText(requireContext(), "Save PDF clicked - implement PDF export", Toast.LENGTH_SHORT).show()
    }

    private fun onSaveCsvClicked() {
        // TODO: Add your logic to save call log as CSV here
        Toast.makeText(requireContext(), "Save CSV clicked - implement CSV export", Toast.LENGTH_SHORT).show()
    }
}