package com.quantasis.calllog.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.adapter.DetailedAnalysisAdapter
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.datamodel.StatType
import com.quantasis.calllog.repository.CallerDashboardRepository
import com.quantasis.calllog.ui.CallerDashboardActivity
import com.quantasis.calllog.ui.TopCallerReportActivity
import com.quantasis.calllog.viewModel.AnalysisDetailedViewModel
import com.quantasis.calllog.viewModel.AnalysisDetailedViewModelFactory

class AnalysisDetailedFragment : Fragment() {

    private lateinit var viewModel: AnalysisDetailedViewModel
    private lateinit var adapter: DetailedAnalysisAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout manually (assumed layout name: fragment_detailed_analysis.xml)
        return inflater.inflate(R.layout.fragment_detailed_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize recyclerView using findViewById
        recyclerView = view.findViewById(R.id.recyclerView) // Ensure the ID matches your XML layout

        val dao = AppDatabase.getInstance(requireContext().applicationContext).callLogDao()
        val repository = CallerDashboardRepository(dao)

        val factory = AnalysisDetailedViewModelFactory(requireActivity().application, repository)
        viewModel = ViewModelProvider(this, factory)[AnalysisDetailedViewModel::class.java]

        adapter = DetailedAnalysisAdapter { type,number,name ->

            when (type) {
                StatType.TOP_10_CALLERS, StatType.TOP_10_INCOMING, StatType.TOP_10_OUTGOING,
                StatType.TOP_10_DURATION, StatType.TOP_10_INCOMING_DURATION, StatType.TOP_10_OUTGOING_DURATION
                -> {
                    // Handle stat card click (e.g., navigate to detail screen)
                    val intent = Intent(context, TopCallerReportActivity::class.java)
                    intent.putExtra("start_type", type)
                    startActivity(intent)
                }


                StatType.LONGEST_CALL, StatType.TOP_TOTAL_CALLS, StatType.HIGHEST_TOTAL_CALL_DURATION
                -> {
                    val intent = Intent(context, CallerDashboardActivity::class.java)
                    intent.putExtra("number", number)
                    intent.putExtra("name", name)
                    startActivity(intent)
                }
            }


        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter

        observeViewModel()
        viewModel.loadData(null, null)
    }

    private fun observeViewModel() {
        viewModel.statistics.observe(viewLifecycleOwner) { stats ->
            adapter.submitList(stats)
        }
    }
}