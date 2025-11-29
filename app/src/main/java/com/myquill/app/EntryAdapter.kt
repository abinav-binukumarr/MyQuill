package com.myquill.app

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.Date

class EntryAdapter(private val listener: OnItemClick) : RecyclerView.Adapter<EntryAdapter.Holder>() {

    interface OnItemClick {
        fun onItemClick(entryId: Long)
    }

    private val items = mutableListOf<Entry>()

    fun submit(entries: List<Entry>) {
        items.clear()
        items.addAll(entries)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entry, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val entry = items[position]
        holder.title.text = entry.title
        holder.body.text = entry.body
        holder.date.text = DateFormat.getDateTimeInstance().format(Date(entry.createdAt))

        if (!entry.imageUri.isNullOrEmpty()) {
            holder.image.visibility = View.VISIBLE
            holder.image.setImageURI(Uri.parse(entry.imageUri))
        } else {
            holder.image.setImageDrawable(null)
            holder.image.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            listener.onItemClick(entry.id)
        }
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.itemTitle)
        val body: TextView = view.findViewById(R.id.itemBody)
        val date: TextView = view.findViewById(R.id.itemDate)
        val image: ImageView = view.findViewById(R.id.itemImage)
    }
}
