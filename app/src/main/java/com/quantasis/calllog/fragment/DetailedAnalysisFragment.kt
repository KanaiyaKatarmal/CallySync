package com.quantasis.calllog.fragment

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
import com.quantasis.calllog.repository.CallerDashboardRepository
import com.quantasis.calllog.viewModel.DetailedAnalysisViewModel
import com.quantasis.calllog.viewModel.DetailedAnalysisViewModelFactory

class DetailedAnalysisFragment : Fragment() {

    private lateinit var viewModel: DetailedAnalysisViewModel
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

        val factory = DetailedAnalysisViewModelFactory(requireActivity().application, repository)
        viewModel = ViewModelProvider(this, factory)[DetailedAnalysisViewModel::class.java]

        adapter = DetailedAnalysisAdapter { statType ->
            // Handle stat card click (e.g., navigate to detail screen)
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