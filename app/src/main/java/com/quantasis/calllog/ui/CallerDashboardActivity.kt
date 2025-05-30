package com.quantasis.calllog.ui
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.quantasis.calllog.R
import pageradapter.CallerDashboardPagerAdapter
import com.quantasis.calllog.database.AppDatabase

class CallerDashboardActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: CallerDashboardPagerAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caller_dashboard)  // Your splash layout here

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)

        val name = intent.getStringExtra("name")!!
        val number = intent.getStringExtra("number")!!

        adapter = CallerDashboardPagerAdapter(this, name, number)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Overview"
                1 -> "Call History"
                else -> "Tab ${position + 1}"
            }
        }.attach()
    }


}