package com.quantasis.calllog.ui


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.quantasis.calllog.R

class PermissionActivity : AppCompatActivity() {

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CONTACTS
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            openMainActivity()
        } else {
            val someDeniedPermanently = requiredPermissions.any {
                !ActivityCompat.shouldShowRequestPermissionRationale(this, it)
                        && ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (someDeniedPermanently) {
                startActivity(Intent(this, PermissionSettingsActivity::class.java))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Check if all permissions are already granted
        if (areAllPermissionsGranted()) {
            openMainActivity()
            return
        }

        setContentView(R.layout.activity_permission)

        findViewById<Button>(R.id.btnAllowAccess).setOnClickListener {
            val notGranted = requiredPermissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            if (notGranted.isEmpty()) {
                openMainActivity()
            } else {
                permissionLauncher.launch(notGranted.toTypedArray())
            }
        }
    }

    private fun areAllPermissionsGranted(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun openMainActivity() {
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }
}