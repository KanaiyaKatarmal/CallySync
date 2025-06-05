package com.quantasis.calllog.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.quantasis.calllog.R
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.manager.CallLogSyncManager
import com.quantasis.calllog.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)  // Your splash layout here

        // Initialize Room database singleton
        db = AppDatabase.getInstance(applicationContext)

        // Launch coroutine to sync call logs, then open main screen
        lifecycleScope.launch {

            try {
                withContext(Dispatchers.IO) {
                    ContactRepository(applicationContext, db.contactDao()).syncContactsWithDevice()
                }
            } catch (e: Exception) {
                e.printStackTrace()  // Log any errors during sync
            }

            try {
                CallLogSyncManager.syncCallLogsToRoom(applicationContext, db)
            } catch (e: Exception) {
                e.printStackTrace()  // Log any errors during sync
            }


            openMainActivity()
        }

        /*lifecycleScope.launch(Dispatchers.IO) {
            ContactRepository(applicationContext, db.contactDao()).syncContactsWithDevice()
        }*/
    }

    private fun openMainActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()  // Close splash so user can't go back here
    }
}