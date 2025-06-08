package com.quantasis.calllog.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.quantasis.calllog.R
import com.quantasis.calllog.adapter.CallerDashboardOverviewAdapter
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.datamodel.CallerDashboardData
import com.quantasis.calllog.repository.CallerDashboardRepository
import com.quantasis.calllog.util.CallConvertUtil
import com.quantasis.calllog.viewModel.CallerDashboardOverviewViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CallerDashboardOverviewFragment : Fragment() {

    private lateinit var viewModel: CallerDashboardOverviewViewModel
    private lateinit var pieChartCalls: PieChart
    private lateinit var dateRangeText: TextView
    private lateinit var pieChartDuration: PieChart
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CallerDashboardOverviewAdapter

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_caller_dashboard_overview, container, false)
    }

    companion object {
        private const val ARG_MOB_NO = "mobile_number"
        private const val ARG_NAME= "name"
        private const val ARG_START_DATE= "start_date"
        private const val ARG_END_DATE= "end_date"

        fun newInstance (name: String, number: String,startDate: Date? =null,endDate: Date?=null): CallerDashboardOverviewFragment {
            return CallerDashboardOverviewFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_MOB_NO, number)
                    putSerializable(ARG_NAME, name)
                    putSerializable(ARG_START_DATE, startDate)
                    putSerializable(ARG_END_DATE, endDate)

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateRangeText =  view.findViewById(R.id.dateRangeText)
        pieChartCalls = view.findViewById(R.id.pieChartCalls)
        pieChartDuration = view.findViewById(R.id.pieChartDuration)
        recyclerView = view.findViewById(R.id.statsRecyclerView)
        adapter = CallerDashboardOverviewAdapter()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        pieChartCalls.apply {
            isDrawHoleEnabled = false  // ❌ No white hole
            setUsePercentValues(true)
            description.isEnabled = true
            setEntryLabelColor(Color.BLACK)
            setDrawEntryLabels(true)
            legend.isEnabled = false
            animateY(1000)

        }


        pieChartDuration.apply {
            isDrawHoleEnabled = false  // ❌ No white hole
            setUsePercentValues(true)
            description.isEnabled = true
            setEntryLabelColor(Color.BLACK)
            setDrawEntryLabels(true)
            legend.isEnabled = false
            animateY(1000)
        }



        val repository = CallerDashboardRepository(AppDatabase.getInstance(requireContext()).callLogDao())
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CallerDashboardOverviewViewModel(repository) as T
            }
        })[CallerDashboardOverviewViewModel::class.java]

        val mobileNumber = arguments?.getString(CallerDashboardOverviewFragment.ARG_MOB_NO) ?: return
        val startDate = arguments?.getSerializable(CallerDashboardOverviewFragment.ARG_START_DATE) as? Date
        val endDate = arguments?.getSerializable(CallerDashboardOverviewFragment.ARG_END_DATE) as? Date


        if (startDate == null || endDate == null) {
            dateRangeText.text = "No Date ange Selected"
        } else {
            dateRangeText.text = "${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"
        }
        viewModel.loadDashboardData(mobileNumber, startDate, endDate)

        viewModel.dashboardData.observe(viewLifecycleOwner) { data ->
            adapter.submitList(data)
            updatePieChart(data)
            setupPieChartDuration(data)
        }
    }

    private fun updatePieChart(data: List<CallerDashboardData>) {
        val filteredData = data.filter { it.callCategory != 999 }

        val entries = filteredData.map { PieEntry(it.count.toFloat(), it.callCategory) }
        val colors = filteredData.map { CallConvertUtil.getColor(it.callCategory) }

        val dataSet = PieDataSet(entries, "Call Type")
        dataSet.colors = colors

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(object : ValueFormatter() {
            override fun getPieLabel(value: Float, pieEntry: PieEntry): String {
                // You can customize this string format
                val actualValue = pieEntry.value // The actual value
                return String.format("%.1f%% (%.0f)", value, actualValue)
            }
        })
        pieChartCalls.data = pieData
        pieChartCalls.invalidate()
    }

    private fun setupPieChartDuration(durationStats: List<CallerDashboardData>) {

        val filteredData = durationStats.filter { it.callCategory != 999 && it.totalDuration > 0}

        val entries = filteredData.map { PieEntry(it.totalDuration.toFloat(), it.callCategory) }
        val colors = filteredData.map { CallConvertUtil.getColor(it.callCategory) }

        val dataSet = PieDataSet(entries, "Call Duration by Type")
        dataSet.colors = colors

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(object : ValueFormatter() {
            override fun getPieLabel(value: Float, pieEntry: PieEntry): String {
                // You can customize this string format
                val actualValue = pieEntry.value // The actual value
                return String.format("%.1f%% (%s)", value, CallConvertUtil.formatDuration(
                    actualValue.toInt()
                ))
            }
        })
        pieChartDuration.data = pieData
        pieChartDuration.invalidate()
    }
}