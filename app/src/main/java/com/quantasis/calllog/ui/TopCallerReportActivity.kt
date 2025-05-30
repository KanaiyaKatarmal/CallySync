package com.quantasis.calllog.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.adapter.TopCallerReportAdapter
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.datamodel.StatType
import com.quantasis.calllog.repository.CallLogRepository
import com.quantasis.calllog.viewModel.TopCallerReportViewModel
import com.quantasis.calllog.viewModel.TopCallerReportViewModelFactory

class TopCallerReportActivity : AppCompatActivity() {

    private lateinit var viewModel: TopCallerReportViewModel
    private lateinit var adapter: TopCallerReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_caller_summary)

        val type = intent.getSerializableExtra("start_type") as? StatType
            ?: StatType.TOP_10_CALLERS // default fallback

        val dao = AppDatabase.getInstance(this).callLogDao()
        val repo = CallLogRepository(dao)
        val factory = TopCallerReportViewModelFactory(repo, type)
        viewModel = ViewModelProvider(this, factory)[TopCallerReportViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.summaryRecyclerView)
        adapter = TopCallerReportAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.data.observe(this) {
            adapter.submitList(it)
        }
    }


}