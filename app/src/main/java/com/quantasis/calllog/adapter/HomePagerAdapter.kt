package com.quantasis.calllog.adapter

import AllCallTabFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.quantasis.calllog.fragment.BlockedNumberCallTabFragment
import com.quantasis.calllog.fragment.CallLogFragment
import com.quantasis.calllog.fragment.IncomingCallTabFragment
import com.quantasis.calllog.fragment.MissedCallTabFragment
import com.quantasis.calllog.fragment.NotAttendedCallTabFragment
import com.quantasis.calllog.fragment.NotPickedCallTabFragment
import com.quantasis.calllog.fragment.OutgoingCallTabFragment
import com.quantasis.calllog.fragment.RejectedCallTabFragment
import com.quantasis.calllog.fragment.UnknownNumberCallTabFragment

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 9 // number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CallLogFragment()
            1 -> IncomingCallTabFragment()
            2 -> OutgoingCallTabFragment()
            3 -> MissedCallTabFragment()
            4 -> RejectedCallTabFragment()
            5 -> NotPickedCallTabFragment()
            6 -> NotAttendedCallTabFragment()
            7 -> UnknownNumberCallTabFragment()
            8 -> BlockedNumberCallTabFragment()
            else -> Fragment()
        }
    }
}