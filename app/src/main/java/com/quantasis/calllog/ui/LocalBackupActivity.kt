package com.quantasis.calllog.ui


import android.os.Bundle
import android.os.Environment
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
            startBackup();
        }

        findViewById<Button>(R.id.btnRestoreFromPhone).setOnClickListener {
            Toast.makeText(this, "Restore From Phone clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startBackup() {
        val backUpFilePath: File= Environment.getExternalStoragePublicDirectory(
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

        try {
            file.writeText("hello")
            Toast.makeText(this, "File saved at: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            refreshBackupList()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_LONG).show()
        }
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

}