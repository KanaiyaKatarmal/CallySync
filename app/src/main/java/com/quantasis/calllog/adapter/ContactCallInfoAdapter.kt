package com.quantasis.calllog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quantasis.calllog.R
import com.quantasis.calllog.datamodel.ContactCallInfo

class ContactCallInfoAdapter(
    private val onItemClick: (ContactCallInfo) -> Unit
)  : RecyclerView.Adapter<ContactCallInfoAdapter.ContactViewHolder>() {

    private val items = mutableListOf<ContactCallInfo>()

    fun submitList(newItems: List<ContactCallInfo>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.contact_name)
        private val phoneText: TextView = itemView.findViewById(R.id.contact_phone)
        private val callCountText: TextView = itemView.findViewById(R.id.call_count)
        private val photo: ImageView = itemView.findViewById(R.id.contact_photo)

        fun bind(item: ContactCallInfo) {
            itemView.setOnClickListener {
                onItemClick(item)
            }
            nameText.text = item.contactName ?: "Unknown"
            phoneText.text = item.phone
            callCountText.text = "Calls: ${item.callCount}"

            if (item.photoUri != null) {
                Glide.with(itemView.context)
                    .load(item.photoUri)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(photo)
            } else {
                photo.setImageResource(R.drawable.ic_launcher_background)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact_call_info, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(items[position])
    }
}