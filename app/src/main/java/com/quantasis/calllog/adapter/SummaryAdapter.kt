package com.quantasis.calllog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.datamodel.CallSummary
import com.quantasis.calllog.util.CallConvertUtil

class SummaryAdapter : RecyclerView.Adapter<SummaryAdapter.ViewHolder>() {

    private var items: List<CallSummary> = listOf()

    fun submitList(data: List<CallSummary>) {
        items = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.label)
        val count: TextView = view.findViewById(R.id.callCount)
        val duration: TextView = view.findViewById(R.id.callDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_call_summary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.label.text = item.label
        holder.count.text = "Calls: ${item.count}"
        holder.duration.text = "Duration: ${CallConvertUtil.formatDuration(item.duration.toInt())}"
    }

    override fun getItemCount() = items.size
}