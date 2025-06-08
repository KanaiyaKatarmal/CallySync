package pageradapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.quantasis.calllog.fragment.CallLogListFragment
import com.quantasis.calllog.fragment.CallerDashboardOverviewFragment
import com.quantasis.calllog.repository.CallLogPageType
import java.util.Date

class CallerDashboardPagerAdapter(fragment: FragmentActivity, val name: String, val number: String, val startDate: Date?, val endDate: Date?) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2 // number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CallerDashboardOverviewFragment.newInstance(name,number,startDate = startDate,endDate = endDate)
            1 -> CallLogListFragment.newInstance(type=CallLogPageType.ALL,number=number, startDate = startDate,endDate = endDate)
            else -> Fragment()
        }
    }
}