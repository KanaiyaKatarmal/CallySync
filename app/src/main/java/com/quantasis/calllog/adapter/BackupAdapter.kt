package com.quantasis.calllog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.datamodel.BackupItem

class BackupAdapter(
    private var items: List<BackupItem> = listOf(),
    private val onRestoreClick: (BackupItem) -> Unit,
    private val onDeleteClick: (BackupItem) -> Unit
) : RecyclerView.Adapter<BackupAdapter.BackupViewHolder>() {

    inner class BackupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileNameText: TextView = view.findViewById(R.id.fileNameText)
        val fileLocationText: TextView = view.findViewById(R.id.fileLocationText)
        val fileDateText: TextView = view.findViewById(R.id.fileDateText)
        val btnRestore: TextView = view.findViewById(R.id.btnRestore)
        val btnDelete: TextView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_backup, parent, false)
        return BackupViewHolder(view)
    }

    override fun onBindViewHolder(holder: BackupViewHolder, position: Int) {
        val item = items[position]
        holder.fileNameText.text = item.fileName
        holder.fileLocationText.text = "Location: ${item.fileLocation}"
        holder.fileDateText.text = "Date: ${item.fileDate}"

        holder.btnRestore.setOnClickListener { onRestoreClick(item) }
        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateData(newList: List<BackupItem>) {
        items = newList
        notifyDataSetChanged()
    }

    /*fun updateList(newList: List<BackupItem>) {
        (items as MutableList).clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }*/
}