package com.quantasis.calllog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.database.CallLogEntity
import com.quantasis.calllog.interfacecallback.OnCallLogItemClickListener
import com.quantasis.calllog.util.CallConvertUtil
import com.quantasis.calllog.viewModel.CallLogUiModel

class CallLogListAdapter (
    private val listener: OnCallLogItemClickListener,private val addNotlistener: OnCallLogItemClickListener
): PagingDataAdapter<CallLogUiModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

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
            else -> CallLogViewHolder(inflater.inflate(R.layout.item_call_log, parent, false),listener,addNotlistener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val model = getItem(position)) {
            is CallLogUiModel.Item -> (holder as CallLogViewHolder).bind(model.entry)
            is CallLogUiModel.DateSeparator -> (holder as DateHeaderViewHolder).bind(model.date)
            else -> {}
        }
    }

    class CallLogViewHolder(view: View,private val listener: OnCallLogItemClickListener,private val addNotlistener: OnCallLogItemClickListener) : RecyclerView.ViewHolder(view) {
        fun bind(entry: CallLogEntity) {
            itemView.findViewById<TextView>(R.id.nameTextView).text = entry.name ?: "Unknown"
            itemView.findViewById<TextView>(R.id.numberTextView).text = entry.rawNumber
            itemView.findViewById<TextView>(R.id.durationTextView).text = "Duration: ${CallConvertUtil.formatDuration(entry.duration)}"

            itemView.findViewById<TextView>(R.id.dateTextView).text = "Date: ${CallConvertUtil.formatDate(entry.date)}"

            itemView.findViewById<TextView>(R.id.callTypeTextView).text = "Type: ${CallConvertUtil.callTypeToString(entry.callType)}"

            itemView.findViewById<TextView>(R.id.numberTextView).setOnClickListener {
                addNotlistener.onItemClick(entry)
            }

            itemView.setOnClickListener {
                listener.onItemClick(entry)
            }
        }

    }

    class DateHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(date: String) {
            itemView.findViewById<TextView>(R.id.dateHeaderTextView).text = date
        }
    }
}