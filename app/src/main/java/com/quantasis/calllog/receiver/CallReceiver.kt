package com.quantasis.calllog.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.manager.CallLogSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Check permissions
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        // Filter duplicates and rapid successive events
        val now = System.currentTimeMillis()
        if (state == null || state == lastState && now - lastEventTime < 500) {
            return
        }
        lastState = state
        lastEventTime = now
        when (state) {
            TelephonyManager.EXTRA_STATE_IDLE -> {
                updateCallLogAsync(context)
            }
        }
    }

    companion object {
        private const val TAG = "CallReceiver"
        private var lastState = ""
        private var lastEventTime: Long = 0
    }

    private fun updateCallLogAsync(context: Context) {
        try {
            GlobalScope.launch(Dispatchers.IO) {
                CallLogSyncManager.syncCallLogsToRoom(context, AppDatabase.getInstance(context))
            }

        } catch (e: Exception) {
            e.printStackTrace()  // Log any errors during sync
        }
    }

}