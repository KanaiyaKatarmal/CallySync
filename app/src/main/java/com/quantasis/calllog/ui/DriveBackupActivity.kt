package com.quantasis.calllog.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.adapter.BackupAdapter
import com.quantasis.calllog.datamodel.BackupItem

class DriveBackupActivity : AppCompatActivity() {

    private lateinit var backupRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive_backup)

        val sampleData = listOf(
            BackupItem("Backup_1748697332450", "/storage/emulated/0/backups/", "31 May 2025 18:45:32"),
            BackupItem("Backup_1748697332282", "/storage/emulated/0/backups/", "31 May 2025 18:45:23"),
            BackupItem("Backup_1748697099050", "/storage/emulated/0/backups/", "31 May 2025 18:41:39")
        )

        backupRecyclerView = findViewById(R.id.backupRecyclerView)
        backupRecyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = BackupAdapter(
            sampleData,
            onRestoreClick = { item ->
                Toast.makeText(this, "Restoring ${item.fileName}", Toast.LENGTH_SHORT).show()
                // perform restore action
            },
            onViewClick = { item ->
                Toast.makeText(this, "Viewing ${item.fileName}", Toast.LENGTH_SHORT).show()
                // open backup details
            },
            onUploadClick = { item ->
                Toast.makeText(this, "Uploading ${item.fileName}", Toast.LENGTH_SHORT).show()
                // upload logic
            },
            onDeleteClick = { item ->
                Toast.makeText(this, "Deleting ${item.fileName}", Toast.LENGTH_SHORT).show()
                // delete logic
            }
        )

        backupRecyclerView.adapter = adapter

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.backupNow).setOnClickListener {
            Toast.makeText(this, "Backup Now clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnDriveLogin).setOnClickListener {
            Toast.makeText(this, "Google Drive Login clicked", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnDriveLogout).setOnClickListener {
            Toast.makeText(this, "Google Drive Logout clicked", Toast.LENGTH_SHORT).show()
        }


    }
}