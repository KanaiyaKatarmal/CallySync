package com.quantasis.calllog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.database.CallLogEntryEntity
import java.text.SimpleDateFormat
import java.util.*

class AllCallLogAdapter : RecyclerView.Adapter<AllCallLogAdapter.CallLogViewHolder>() {

    private var callLogs: List<CallLogEntryEntity> = emptyList()

    fun setData(newList: List<CallLogEntryEntity>) {
        callLogs = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_call_log_item, parent, false)
        return CallLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        val callLog = callLogs[position]
        holder.bind(callLog)
    }

    override fun getItemCount(): Int = callLogs.size

    class CallLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvNumber: TextView = itemView.findViewById(R.id.tvNumber)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        private val tvCallType: TextView = itemView.findViewById(R.id.tvCallType)

        fun bind(callLog: CallLogEntryEntity) {
            tvName.text = callLog.name ?: "Unknown"
            tvNumber.text = callLog.number
            tvDate.text = formatDate(callLog.date)
            tvDuration.text = formatDuration(callLog.duration)
            tvCallType.text = callTypeToString(callLog.callType)
        }

        private fun formatDate(date: Date?): String {
            return if (date != null) {
                val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                sdf.format(date)
            } else {
                ""
            }
        }

        private fun formatDuration(durationSeconds: Int): String {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return "${minutes}m ${seconds}s"
        }

        private fun callTypeToString(callType: Int): String {
            return when (callType) {
                1 -> "Incoming"
                2 -> "Outgoing"
                3 -> "Missed"
                4 -> "Voicemail"
                5 -> "Rejected"
                6 -> "Blocked"
                7 -> "Answered Externally"
                else -> "Unknown"
            }
        }
    }
}