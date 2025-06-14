package com.quantasis.calllog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.database.CallLogEntity
import com.quantasis.calllog.interfacecallback.OnCallLogItemClickListener
import com.quantasis.calllog.util.CallConvertUtil
import com.quantasis.calllog.viewModel.CallLogUiModel

class CallLogListAdapter(
    private val listener: OnCallLogItemClickListener,
    private val addNotlistener: OnCallLogItemClickListener
) : PagingDataAdapter<CallLogUiModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_HEADER = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CallLogUiModel>() {
            override fun areItemsTheSame(oldItem: CallLogUiModel, newItem: CallLogUiModel): Boolean {
                return when {
                    oldItem is CallLogUiModel.Item && newItem is CallLogUiModel.Item -> oldItem.entry.id == newItem.entry.id
                    oldItem is CallLogUiModel.DateSeparator && newItem is CallLogUiModel.DateSeparator -> oldItem.date == newItem.date
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: CallLogUiModel, newItem: CallLogUiModel): Boolean {
                return oldItem == newItem
            }
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
            else -> CallLogViewHolder(inflater.inflate(R.layout.item_call_log, parent, false), listener, addNotlistener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val model = getItem(position)) {
            is CallLogUiModel.Item -> (holder as CallLogViewHolder).bind(model.entry)
            is CallLogUiModel.DateSeparator -> (holder as DateHeaderViewHolder).bind(model.date)
            else -> {}
        }
    }

    class CallLogViewHolder(
        view: View,
        private val listener: OnCallLogItemClickListener,
        private val addNotlistener: OnCallLogItemClickListener
    ) : RecyclerView.ViewHolder(view) {

        // Caching views
        private val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        private val numberTextView: TextView = view.findViewById(R.id.numberTextView)
        private val durationTextView: TextView = view.findViewById(R.id.durationTextView)
        private val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        private val callTypeTextView: TextView = view.findViewById(R.id.callTypeTextView)
        private val noteTextView: TextView = view.findViewById(R.id.noteTextView)
        private val tagsTextView: TextView = view.findViewById(R.id.tagsTextView)
        private val emptyNoteTagTextView: TextView = view.findViewById(R.id.emptyNoteTagTextView)
        private val noteTagLayout: LinearLayout = view.findViewById(R.id.noteTagLayout)



        fun bind(entry: CallLogEntity) {
            nameTextView.text = entry.name ?: "Unknown"
            numberTextView.text = entry.rawNumber
            durationTextView.text = "Duration: ${CallConvertUtil.formatDuration(entry.duration)}"
            dateTextView.text = "Date: ${CallConvertUtil.formatDate(entry.date)}"
            callTypeTextView.text = "Type: ${CallConvertUtil.callTypeToString(entry.callType)}"


            val hasNote = !entry.note.isNullOrEmpty()
            val hasTags = !entry.tags.isNullOrEmpty()

            if (hasNote) {
                noteTextView.text = "Note: ${entry.note}"
                noteTextView.visibility = View.VISIBLE
            } else {
                noteTextView.visibility = View.GONE
            }

            if (hasTags) {
                tagsTextView.text = "Tags: ${entry.tags}"
                tagsTextView.visibility = View.VISIBLE
            } else {
                tagsTextView.visibility = View.GONE
            }

            // Show Add Note and Tag if both are empty
            if (!hasNote && !hasTags) {
                emptyNoteTagTextView.visibility = View.VISIBLE
            } else {
                emptyNoteTagTextView.visibility = View.GONE
            }

            noteTagLayout.setOnClickListener { addNotlistener.onItemClick(entry) }
            itemView.setOnClickListener { listener.onItemClick(entry) }

        }
    }

    class DateHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dateHeaderTextView: TextView = view.findViewById(R.id.dateHeaderTextView)
        fun bind(date: String) {
            dateHeaderTextView.text = date
        }
    }
}