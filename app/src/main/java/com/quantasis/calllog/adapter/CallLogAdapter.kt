package com.quantasis.calllog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.database.CallLogEntryEntity
import com.quantasis.calllog.viewModel.CallLogUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CallLogAdapter : PagingDataAdapter<CallLogUiModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_HEADER = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CallLogUiModel>() {
            override fun areItemsTheSame(old: CallLogUiModel, new: CallLogUiModel) = old == new
            override fun areContentsTheSame(old: CallLogUiModel, new: CallLogUiModel) = old == new
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CallLogUiModel.Item -> TYPE_ITEM
            is CallLogUiModel.DateSeparator -> TYPE_HEADER
            null -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> DateHeaderViewHolder(inflater.inflate(R.layout.item_date_separator, parent, false))
            else -> CallLogViewHolder(inflater.inflate(R.layout.item_call_log, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val model = getItem(position)) {
            is CallLogUiModel.Item -> (holder as CallLogViewHolder).bind(model.entry)
            is CallLogUiModel.DateSeparator -> (holder as DateHeaderViewHolder).bind(model.date)
            else -> {}
        }
    }

    class CallLogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(entry: CallLogEntryEntity) {
            itemView.findViewById<TextView>(R.id.nameTextView).text = entry.name ?: "Unknown"
            itemView.findViewById<TextView>(R.id.numberTextView).text = entry.rawNumber
            itemView.findViewById<TextView>(R.id.durationTextView).text = "Duration: ${formatDuration(entry.duration)}"

            itemView.findViewById<TextView>(R.id.dateTextView).text = "Date: ${formatDate(entry.date)}"

            itemView.findViewById<TextView>(R.id.callTypeTextView).text = "Type: ${callTypeToString(entry.callType)}"
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

    class DateHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(date: String) {
            itemView.findViewById<TextView>(R.id.dateHeaderTextView).text = date
        }
    }
}