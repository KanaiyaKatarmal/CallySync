package pageradapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.quantasis.calllog.fragment.CallLogListFragment
import com.quantasis.calllog.fragment.CallerDashboardOverviewFragment
import com.quantasis.calllog.repository.CallLogPageType

class CallerDashboardPagerAdapter(fragment: FragmentActivity, val name: String, val number: String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2 // number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CallerDashboardOverviewFragment.newInstance(name,number)
            1 -> CallLogListFragment.newInstance(CallLogPageType.INCOMING)
            else -> Fragment()
        }
    }
}