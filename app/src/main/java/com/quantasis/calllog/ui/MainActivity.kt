package com.quantasis.calllog.ui

import android.Manifest
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


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: CallLogViewModel

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.all { it.value }
        if (granted) {
            viewModel.loadCallLogs()
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
            viewModel.loadCallLogs()
        } else {
            permissionLauncher.launch(notGranted.toTypedArray())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        viewModel = ViewModelProvider(this)[CallLogViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.callLogRecyclerView)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        checkPermissionsAndLoad()
        val loadingView = findViewById<View>(R.id.loadingView)

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadCallLogs()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
            swipeRefreshLayout.isRefreshing = isLoading
        }



        viewModel.callLogs.observe(this) { logs ->
            recyclerView.adapter = CallLogAdapter(logs)
        }

        viewModel.filteredLogs.observe(this){ calLogs ->
            recyclerView.adapter = CallLogAdapter(calLogs)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Search by name"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchLogs(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchLogs(newText ?: "")
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.in_answered -> {
                viewModel.filterCallLogs("Incoming Answered")
                return true
            }

            R.id.in_rejected -> {
                viewModel.filterCallLogs("Rejected")
                return true
            }

            R.id.in_missed -> {
                viewModel.filterCallLogs("Incoming Missed")
                return true
            }

            R.id.out_answered -> {
                viewModel.filterCallLogs("Outgoing Answered")
                return true
            }

            R.id.out_unanswered -> {
                viewModel.filterCallLogs("Outgoing Unanswered")
                return true
            }
            R.id.all ->{
                viewModel.filterCallLogs("")
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}
