package com.quantasis.calllog.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.quantasis.calllog.R
import com.quantasis.calllog.adapter.BackupAdapter
import com.quantasis.calllog.datamodel.BackupItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DriveBackupActivity : AppCompatActivity() {

    private lateinit var backupRecyclerView: RecyclerView
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var driveService: Drive
    private lateinit var tvEmail: TextView
    private lateinit var btnDriveLogin: Button
    private lateinit var btnDriveLogout: Button
    private lateinit var account: GoogleSignInAccount
    private var progressDialog: AlertDialog? = null

    companion object {
        private const val RC_SIGN_IN = 9001
        private val SCOPES = listOf(DriveScopes.DRIVE_FILE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive_backup)

        backupRecyclerView = findViewById(R.id.backupRecyclerView)
        tvEmail = findViewById(R.id.tvEmail)
        btnDriveLogin = findViewById(R.id.btnDriveLogin)
        btnDriveLogout = findViewById(R.id.btnDriveLogout)

        backupRecyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.backupNow).setOnClickListener {
            val fileName = "Backup_${System.currentTimeMillis()}.txt"
            val content = "This is a sample backup file created at ${SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(Date())}"
            saveFileToDrive(fileName, content)
        }

        btnDriveLogin.setOnClickListener { signIn() }
        btnDriveLogout.setOnClickListener { signOut() }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        checkSignInStatus()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        googleSignInClient.signOut()
            .addOnCompleteListener(this) {
                onSignedOut()
                Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            account = task.getResult(ApiException::class.java)
            initializeDriveService(account)
            onSignedIn(account)
            Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show()
        } catch (e: ApiException) {
            Toast.makeText(this, "Sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeDriveService(account: GoogleSignInAccount) {
        val credential = GoogleAccountCredential.usingOAuth2(this, SCOPES)
        credential.selectedAccount = account.account
        driveService = Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
            .setApplicationName(getString(R.string.app_name))
            .build()
    }

    private fun checkSignInStatus() {
        val lastAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (lastAccount != null) {
            onSignedIn(lastAccount)
        } else {
            onSignedOut()
        }
    }

    private fun onSignedIn(account: GoogleSignInAccount) {
        this.account = account
        initializeDriveService(account)
        btnDriveLogin.visibility = View.GONE
        btnDriveLogout.visibility = View.VISIBLE
        tvEmail.text = account.email
        loadDriveFilesToRecyclerView()
    }

    private fun onSignedOut() {
        btnDriveLogin.visibility = View.VISIBLE
        btnDriveLogout.visibility = View.GONE
        tvEmail.text = "Sign in Google Drive to View or Restore and Create Backup"
        backupRecyclerView.adapter = BackupAdapter(emptyList(), {}, {}, {}, {})
    }

    private fun showLoading(message: String = "Loading...") {
        runOnUiThread {
            if (progressDialog == null) {
                val builder = AlertDialog.Builder(this)
                builder.setView(ProgressBar(this).apply {
                    isIndeterminate = true
                    setPadding(32, 32, 32, 32)
                })
                builder.setMessage(message)
                builder.setCancelable(false)
                progressDialog = builder.create()
            }
            progressDialog?.show()
        }
    }

    private fun hideLoading() {
        runOnUiThread {
            progressDialog?.dismiss()
        }
    }

    private fun saveFileToDrive(fileName: String, fileContent: String) {
        showLoading("Uploading to Drive...")
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val metadata = File().apply {
                    name = fileName
                    mimeType = "text/plain"
                }
                val contentStream = ByteArrayContent.fromString("text/plain", fileContent)
                driveService.files().create(metadata, contentStream)
                    .setFields("id, name")
                    .execute()
                loadDriveFilesToRecyclerView()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                hideLoading()
            }
        }
    }

    private fun loadDriveFilesToRecyclerView() {
        showLoading("Loading files from Drive...")
        lifecycleScope.launch {
            val files = listDriveFiles()
            val adapter = BackupAdapter(
                files,
                onRestoreClick = { Toast.makeText(this@DriveBackupActivity, "Restoring ${it.fileName}", Toast.LENGTH_SHORT).show() },
                onViewClick = { Toast.makeText(this@DriveBackupActivity, "Viewing ${it.fileName}", Toast.LENGTH_SHORT).show() },
                onUploadClick = { Toast.makeText(this@DriveBackupActivity, "Uploading ${it.fileName}", Toast.LENGTH_SHORT).show() },
                onDeleteClick = { Toast.makeText(this@DriveBackupActivity, "Deleting ${it.fileName}", Toast.LENGTH_SHORT).show() }
            )
            backupRecyclerView.adapter = adapter
            hideLoading()
        }
    }

    private suspend fun listDriveFiles(): List<BackupItem> = withContext(Dispatchers.IO) {
        val list = mutableListOf<BackupItem>()
        try {
            val result = driveService.files().list()
                .setSpaces("drive")
                .setFields("files(id, name, modifiedTime)")
                .execute()

            result.files?.forEach { file ->
                val dateStr = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
                    .format(Date(file.modifiedTime.value))
                list.add(BackupItem(file.name, file.id, dateStr))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext list
    }
}