package com.quantasis.calllog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.model.CallLogEntry

class CallLogAdapter(private val callLogs: List<CallLogEntry>) :
    RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_call_log, parent, false)
        return CallLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        val log = callLogs[position]
        holder.name.text = log.name ?: "Unknown"
        holder.number.text = "Number: ${log.number}"
        holder.date.text = "Date: ${log.date}"
        holder.type.text = "Type: ${log.type}"
        holder.subType.text = "Sub-type: ${log.subStatus}"
        holder.duration.text = "Duration: ${log.duration}s"
    }

    override fun getItemCount(): Int = callLogs.size

    class CallLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textName)
        val number: TextView = itemView.findViewById(R.id.textNumber)
        val date: TextView = itemView.findViewById(R.id.textDate)
        val type: TextView = itemView.findViewById(R.id.textType)
        val subType: TextView = itemView.findViewById(R.id.subType)
        val duration: TextView = itemView.findViewById(R.id.textDuration)
    }
}
