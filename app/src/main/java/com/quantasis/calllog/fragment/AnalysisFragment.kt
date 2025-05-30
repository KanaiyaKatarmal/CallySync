package com.quantasis.calllog.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.quantasis.calllog.R
import pageradapter.AnalysisPagerAdapter
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.datamodel.CallerDashboardData
import com.quantasis.calllog.repository.CallerDashboardRepository
import com.quantasis.calllog.util.CallConvertUtil
import com.quantasis.calllog.viewModel.AnalysisViewModel
import com.quantasis.calllog.viewModel.AnalysisViewModelFactory
import kotlinx.coroutines.launch
import java.util.Date
class AnalysisFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var pieChartDuration: PieChart

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private lateinit var mobileNumber: String
    private var startDate: Date? = null
    private var endDate: Date? = null

    private lateinit var viewModel: AnalysisViewModel

    companion object {
        fun newInstance(mobileNumber: String, startDate: Date?, endDate: Date?): AnalysisFragment {
            return AnalysisFragment().apply {
                arguments = Bundle().apply {
                    putString("mobileNumber", mobileNumber)
                    putSerializable("startDate", startDate)
                    putSerializable("endDate", endDate)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = requireArguments()
        mobileNumber = args.getString("mobileNumber") ?: ""
        startDate = args.getSerializable("startDate") as? Date
        endDate = args.getSerializable("endDate") as? Date

        val dao = AppDatabase.getInstance(requireContext()).callLogDao()
        val repository = CallerDashboardRepository(dao)
        viewModel = ViewModelProvider(this, AnalysisViewModelFactory(repository))[AnalysisViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pieChart = view.findViewById(R.id.pieChart)
        pieChartDuration = view.findViewById(R.id.pieChartDuration)
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        setupPieChart()
        setupViewPagerAndTabs()

        loadCallerStats()
    }

    private fun setupPieChart() {
        pieChart.apply {
            description.isEnabled = false
            isRotationEnabled = false
            isDrawHoleEnabled = false  // ❌ No white hole
            setUsePercentValues(true)
            legend.isEnabled = true
        }

        pieChartDuration.apply {
            description.isEnabled = false
            isDrawHoleEnabled = false  // ❌ No white hole
            isRotationEnabled = false
            setUsePercentValues(true)
            legend.isEnabled = true
        }
    }

    private fun setupViewPagerAndTabs() {
        val adapter = AnalysisPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Summary"
                1 -> "Detailed Analysis"
                else -> null
            }
        }.attach()
    }

    private fun loadCallerStats() {
        lifecycleScope.launch {
            val stats = viewModel.getCallerStats(mobileNumber, startDate, endDate)
            updatePieChart(stats)
            updateDurationPieChart(stats)
        }
    }

    private fun updatePieChart(stats: List<CallerDashboardData>) {
        val entries = stats.map {
            PieEntry(it.count.toFloat(), CallConvertUtil.callTypeToString(it.callCategory))
        }
        val colors = stats.map { CallConvertUtil.getColor(it.callCategory) }

        val dataSet = PieDataSet(entries, "Call Types")
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueTextSize(14f)
        data.setValueFormatter(PercentFormatter(pieChart))

        pieChart.data = data
        pieChart.invalidate()
    }

    private fun updateDurationPieChart(stats: List<CallerDashboardData>) {
               val entries = stats
            .filter { it.totalDuration > 0 }
            .map {
                PieEntry(it.totalDuration.toFloat(), CallConvertUtil.callTypeToString(it.callCategory))
            }

        val colors = stats.map { CallConvertUtil.getColor(it.callCategory) }

        val dataSet = PieDataSet(entries, "Call Types")
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueTextSize(14f)
        data.setValueFormatter(PercentFormatter(pieChart))

        pieChartDuration.data = data
        pieChartDuration.invalidate()
    }


}
