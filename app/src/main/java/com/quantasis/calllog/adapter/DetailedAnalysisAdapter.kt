package com.quantasis.calllog.adapter
import androidx.recyclerview.widget.ListAdapter
import com.quantasis.calllog.datamodel.StatCardItem
import com.quantasis.calllog.datamodel.StatType

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R

class DetailedAnalysisAdapter(
    private val onClick: (StatType,String?,String?) -> Unit
) : ListAdapter<StatCardItem, DetailedAnalysisAdapter.StatViewHolder>(DiffCallback()) {

    inner class StatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val value: TextView = itemView.findViewById(R.id.value)
        private val card: View = itemView.findViewById(R.id.card)

        fun bind(item: StatCardItem) {
            title.text = item.title
            value.text = item.value
            card.setOnClickListener {
                onClick(item.statType,item.number,item.value)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stat_card, parent, false)
        return StatViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<StatCardItem>() {
        override fun areItemsTheSame(oldItem: StatCardItem, newItem: StatCardItem): Boolean =
            oldItem.statType == newItem.statType

        override fun areContentsTheSame(oldItem: StatCardItem, newItem: StatCardItem): Boolean =
            oldItem == newItem
    }
}