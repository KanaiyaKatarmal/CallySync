package com.quantasis.calllog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.datamodel.CallerSummary

class TopCallerReportAdapter : RecyclerView.Adapter<TopCallerReportAdapter.ViewHolder>() {

    private val items = mutableListOf<CallerSummary>()

    fun submitList(data: List<CallerSummary>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val tvDetail: TextView = view.findViewById(R.id.tvDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_caller_summary, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name ?: "Unknown"
        holder.tvNumber.text = item.number
        holder.tvDetail.text = "Total: ${item.total}"
    }
}