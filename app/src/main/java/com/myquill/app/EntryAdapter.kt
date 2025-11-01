package com.myquill.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat

class EntryAdapter(private val listener: OnItemClick) : RecyclerView.Adapter<EntryAdapter.VH>() {
    interface OnItemClick { fun onItemClick(entryId: Long) }
    private val items = ArrayList<Entry>()
    fun submit(list: List<Entry>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_entry, parent, false)
        return VH(v)
    }
    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = items[position]
        holder.title.text = e.title
        holder.body.text = e.body
        holder.date.text = DateFormat.getDateTimeInstance().format(java.util.Date(e.createdAt))
        holder.itemView.setOnClickListener { listener.onItemClick(e.id) }
    }
    override fun getItemCount(): Int = items.size
    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.itemTitle)
        val body: TextView = v.findViewById(R.id.itemBody)
        val date: TextView = v.findViewById(R.id.itemDate)
    }
}