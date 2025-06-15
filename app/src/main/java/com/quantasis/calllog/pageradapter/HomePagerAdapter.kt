package com.quantasis.calllog.pageradapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.quantasis.calllog.fragment.CallLogListFragment
import com.quantasis.calllog.repository.CallLogPageType

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 9 // number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CallLogListFragment.newInstance(CallLogPageType.ALL)
            1 -> CallLogListFragment.newInstance(CallLogPageType.INCOMING)
            2 ->  CallLogListFragment.newInstance(CallLogPageType.OUTGOING)
            3 -> CallLogListFragment.newInstance(CallLogPageType.MISSED)
            4 -> CallLogListFragment.newInstance(CallLogPageType.REJECTED)
            5 -> CallLogListFragment.newInstance(CallLogPageType.UNANSWERED_OUTGOING)
            6 -> CallLogListFragment.newInstance(CallLogPageType.LATEST_UNRETURNED_MISSED)
            7 -> CallLogListFragment.newInstance(CallLogPageType.UNKNOWN)
            8 -> CallLogListFragment.newInstance(CallLogPageType.BLOCKED)
            else -> Fragment()
        }
    }
}