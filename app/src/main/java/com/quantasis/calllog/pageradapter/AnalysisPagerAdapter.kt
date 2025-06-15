package com.quantasis.calllog.pageradapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.quantasis.calllog.fragment.AnalysisDetailedFragment
import com.quantasis.calllog.fragment.AnalysisSummaryFragment

class AnalysisPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> AnalysisSummaryFragment()
            1 -> AnalysisDetailedFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}