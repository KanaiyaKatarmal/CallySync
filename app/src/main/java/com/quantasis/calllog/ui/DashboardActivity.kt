package com.quantasis.calllog.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.quantasis.calllog.R
import com.quantasis.calllog.fragment.AnalysisFragment
import com.quantasis.calllog.fragment.ContactsFragment
import com.quantasis.calllog.fragment.CallsFragment
import com.quantasis.calllog.fragment.MoreFragment

class DashboardActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        bottomNav = findViewById(R.id.bottom_navigation)

        // Set initial fragment
        loadFragment(CallsFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calls -> loadFragment(CallsFragment())
                R.id.nav_analysis -> loadFragment(AnalysisFragment())
                R.id.nav_contacts -> loadFragment(ContactsFragment())
                R.id.nav_more -> loadFragment(MoreFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}