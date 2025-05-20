package com.quantasis.calllog.ui
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.quantasis.calllog.R
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.manager.CallLogSyncManager
import kotlinx.coroutines.launch

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
                CallLogSyncManager.syncCallLogsToRoom(applicationContext, db)
            } catch (e: Exception) {
                e.printStackTrace()  // Log any errors during sync
            }

            openMainActivity()
        }
    }

    private fun openMainActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()  // Close splash so user can't go back here
    }
}