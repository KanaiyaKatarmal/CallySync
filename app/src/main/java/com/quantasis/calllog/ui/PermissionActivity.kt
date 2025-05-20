package com.quantasis.calllog.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quantasis.calllog.R
import com.quantasis.calllog.adapter.CallLogAdapter
import com.quantasis.calllog.viewModel.CallLogViewModel


class PermissionActivity : AppCompatActivity() {

    private lateinit var viewModel: CallLogViewModel

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.all { it.value }
        if (granted) {
            openMainActivity();
        }
    }

    private fun checkPermissionsAndLoad() {
        val requiredPermissions = arrayOf(
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS
        )

        val notGranted = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isEmpty()) {
            openMainActivity()
        } else {
            permissionLauncher.launch(notGranted.toTypedArray())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissionsAndLoad()
        }

    private fun openMainActivity() {
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()  // Close splash so user can't go back here
    }


}
