package com.quantasis.calllog.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.datamodel.StatType
import com.quantasis.calllog.datamodel.TopCallListItemSummary
import com.quantasis.calllog.ui.CallerDashboardActivity
import com.quantasis.calllog.ui.TopCallerReportActivity
import com.quantasis.calllog.util.CallConvertUtil

class TopCallerReportAdapter( private val type :StatType ,
    private val onClick: (TopCallListItemSummary) -> Unit
) : RecyclerView.Adapter<TopCallerReportAdapter.ViewHolder>() {

    private val items = mutableListOf<TopCallListItemSummary>()

    fun submitList(data: List<TopCallListItemSummary>) {
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

        when (type) {
            StatType.TOP_10_CALLERS, StatType.TOP_10_INCOMING, StatType.TOP_10_OUTGOING
            -> {

                holder.tvDetail.text = "Call: ${item.total}"
            }


            StatType.TOP_10_DURATION, StatType.TOP_10_INCOMING_DURATION, StatType.TOP_10_OUTGOING_DURATION
            -> {

                holder.tvDetail.text = "Duration: ${CallConvertUtil.formatDuration(item.total)}"
            }

            else -> {}
        }


        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }
}