package com.quantasis.calllog.ui


import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.adapter.BackupAdapter
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.database.CallLogEntity
import com.quantasis.calllog.datamodel.BackupItem
import com.quantasis.calllog.fragment.CallLogListFragment
import com.quantasis.calllog.repository.CallLogPageType
import com.quantasis.calllog.repository.DownloadCallLogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class LocalBackupActivity : AppCompatActivity() {

    private lateinit var backupRecyclerView: RecyclerView
    private lateinit var  adapter :BackupAdapter;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_backup)


        // Load backup files from folder
        val backupItems = loadBackupItems()

        backupRecyclerView = findViewById(R.id.backupRecyclerView)
        backupRecyclerView.layoutManager = LinearLayoutManager(this)

           adapter = BackupAdapter(
            backupItems,
            onRestoreClick = { item ->
                restoreBackup(item.fileLocation)
                // perform restore action
            },
            onDeleteClick = { item ->
                deleteBackup(item.fileLocation)
            }
        )

        backupRecyclerView.adapter = adapter

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.backupNow).setOnClickListener {
            startBackup();
        }

        findViewById<Button>(R.id.btnRestoreFromPhone).setOnClickListener {
            Toast.makeText(this, "Restore From Phone clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startBackup() {
        // Launch coroutine because fetchCallLogs is suspend function
        lifecycleScope.launch {
            try {
                // Fetch call logs from database
                val callLogs = fetchCallLogs()

                // Convert list to JSON
                val gson = com.google.gson.Gson()
                val jsonString = gson.toJson(callLogs)

                // Create backup file path
                val backUpFilePath: File = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS + "/" + getString(R.string.app_name) + "/Backup/"
                )
                if (!backUpFilePath.isDirectory) {
                    backUpFilePath.mkdirs()
                }

                val strVideoName = StringBuilder()
                strVideoName.append("BACKUP_")
                strVideoName.append(System.currentTimeMillis().toString())
                strVideoName.append(".txt")

                val file = File(backUpFilePath, strVideoName.toString())

                // Write JSON string to file
                file.writeText(jsonString)

                Toast.makeText(this@LocalBackupActivity, "File saved at: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                refreshBackupList()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@LocalBackupActivity, "Failed to save file", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun fetchCallLogs(): List<CallLogEntity> = withContext(Dispatchers.IO) {
        val dao = AppDatabase.getInstance(applicationContext).downloadCallLogDao()
        val repo = DownloadCallLogRepository(dao)
        repo.getCallLogsList(
            search = null,
            startDate = null,
            endDate = null,
            type = CallLogPageType.ALL
        )
    }


    private fun loadBackupItems(): List<BackupItem> {
        val backUpFilePath: File = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS + "/" + getString(R.string.app_name) + "/Backup/"
        )

        if (!backUpFilePath.isDirectory) {
            backUpFilePath.mkdirs()
        }

        val files = backUpFilePath.listFiles()
        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())

        return files
            ?.sortedByDescending { it.lastModified() } // sort by latest first
            ?.map {
                BackupItem(
                    fileName = it.name,
                    fileLocation = it.absolutePath,
                    fileDate = dateFormat.format(Date(it.lastModified()))
                )
            } ?: emptyList()
    }

    private fun refreshBackupList() {
        val updatedList = loadBackupItems()
        adapter.updateData(updatedList)
    }

    private fun restoreBackup(filePath: String) {
        lifecycleScope.launch {
            try {
                // Read file
                val file = File(filePath)
                val jsonString = file.readText()

                // Convert JSON string to List<CallLogEntity>
                val gson = com.google.gson.Gson()
                val listType = object : com.google.gson.reflect.TypeToken<List<CallLogEntity>>() {}.type
                val callLogList: List<CallLogEntity> = gson.fromJson(jsonString, listType)

                // Insert into DB
                withContext(Dispatchers.IO) {
                    val dao = AppDatabase.getInstance(applicationContext).downloadCallLogDao()
                    dao.insertCallLogs(callLogList)
                }

                Toast.makeText(this@LocalBackupActivity, "Restore completed!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@LocalBackupActivity, "Restore failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteBackup(filePath: String) {
        try {
            val file = File(filePath)
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    Toast.makeText(this, "File deleted successfully", Toast.LENGTH_SHORT).show()
                    refreshBackupList()  // Refresh list after deletion
                } else {
                    Toast.makeText(this, "Failed to delete file", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error while deleting file", Toast.LENGTH_SHORT).show()
        }
    }

}