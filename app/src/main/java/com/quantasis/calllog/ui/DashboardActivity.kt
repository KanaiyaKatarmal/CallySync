package com.quantasis.calllog.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
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
    private var doubleBackToExitPressedOnce = false
    private val exitHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Setup toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomNav = findViewById(R.id.bottom_navigation)

        // Set initial fragment
        loadFragment(CallsFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calls -> loadFragment(CallsFragment())
                R.id.nav_analysis -> loadFragment(AnalysisFragment.newInstance("", null, null)
                )
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

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show()

        exitHandler.postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        exitHandler.removeCallbacksAndMessages(null)
    }
}