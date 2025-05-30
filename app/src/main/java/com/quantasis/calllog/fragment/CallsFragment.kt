package com.quantasis.calllog.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.quantasis.calllog.R
import pageradapter.HomePagerAdapter

class CallsFragment : Fragment() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: HomePagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calls, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)

        adapter = HomePagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "All"
                1 -> "Incoming"
                2 -> "Outgoing"
                3 -> "Missed"
                4 -> "Rejected"
                5 -> "Not Picked Outgoing"
                6 -> "Not Attended Incoming"
                7 -> "Unknown Number"
                8 -> "Blocked Number"
                else -> "Tab ${position + 1}"
            }
        }.attach()
    }
}