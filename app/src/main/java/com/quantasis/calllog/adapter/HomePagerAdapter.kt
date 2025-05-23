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
import com.quantasis.calllog.repository.CallLogPageType

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 9 // number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CallLogFragment.newInstance(CallLogPageType.ALL)
            1 -> CallLogFragment.newInstance(CallLogPageType.INCOMING)
            2 ->  CallLogFragment.newInstance(CallLogPageType.OUTGOING)
            3 -> CallLogFragment.newInstance(CallLogPageType.MISSED)
            4 -> CallLogFragment.newInstance(CallLogPageType.REJECTED)
            5 -> CallLogFragment.newInstance(CallLogPageType.UNANSWERED_OUTGOING)
            6 -> CallLogFragment.newInstance(CallLogPageType.LATEST_UNRETURNED_MISSED)
            7 -> CallLogFragment.newInstance(CallLogPageType.UNKNOWN)
            8 -> CallLogFragment.newInstance(CallLogPageType.BLOCKED)
            else -> Fragment()
        }
    }
}