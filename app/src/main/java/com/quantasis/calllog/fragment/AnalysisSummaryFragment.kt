package com.quantasis.calllog.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.adapter.AnalysisSummaryAdapter
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.repository.CallLogRepository
import com.quantasis.calllog.viewModel.AnalysisSummaryViewModel
import com.quantasis.calllog.viewModel.SummaryViewModelFactory
import java.util.Date

class AnalysisSummaryFragment : Fragment() {

    private lateinit var viewModel: AnalysisSummaryViewModel
    private lateinit var adapter: AnalysisSummaryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dao = AppDatabase.getInstance(requireContext()).callLogDao()
        val repository = CallLogRepository(dao)
        viewModel = ViewModelProvider(this, SummaryViewModelFactory(repository))[AnalysisSummaryViewModel::class.java]

        adapter = AnalysisSummaryAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.summaryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Replace with actual values
        val startDate: Date? = null
        val endDate: Date? = null

        viewModel.loadSummary(startDate, endDate)

        viewModel.summaryData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}