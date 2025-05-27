package com.quantasis.calllog.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.quantasis.calllog.fragment.DetailedAnalysisFragment
import com.quantasis.calllog.fragment.SummaryFragment

class AnalysisPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> SummaryFragment()
            1 -> DetailedAnalysisFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}