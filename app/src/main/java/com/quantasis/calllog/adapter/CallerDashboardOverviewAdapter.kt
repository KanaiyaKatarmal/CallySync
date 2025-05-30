package com.quantasis.calllog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.datamodel.CallerDashboardData
import com.quantasis.calllog.util.CallConvertUtil

class CallerDashboardOverviewAdapter : ListAdapter<CallerDashboardData, CallerDashboardOverviewAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<CallerDashboardData>() {
        override fun areItemsTheSame(oldItem: CallerDashboardData, newItem: CallerDashboardData) =
            oldItem.callCategory == newItem.callCategory

        override fun areContentsTheSame(oldItem: CallerDashboardData, newItem: CallerDashboardData) =
            oldItem == newItem
    }
) {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: CallerDashboardData) {
            itemView.findViewById<TextView>(R.id.callTypeText).text = "Type: ${CallConvertUtil.callTypeToString(item.callCategory)}"
            itemView.findViewById<TextView>(R.id.countText).text = "Count: ${item.count}"
            itemView.findViewById<TextView>(R.id.durationText).text =  "Duration: ${CallConvertUtil.formatDuration(item.totalDuration)}"
            val color = CallConvertUtil.getColor(item.callCategory)
            itemView.findViewById<View>(R.id.colorBox).setBackgroundColor(color)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard_stat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}