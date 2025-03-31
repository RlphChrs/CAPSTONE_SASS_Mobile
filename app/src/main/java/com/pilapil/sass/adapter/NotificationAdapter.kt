package com.pilapil.sass.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pilapil.sass.R
import com.pilapil.sass.model.NotificationResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationAdapter(
    private val items: MutableList<NotificationResponse>,
    private val onItemClick: (NotificationResponse) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_notification_title)
        val date: TextView = view.findViewById(R.id.tv_notification_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.subject
        val seconds = item.timestamp?._seconds ?: 0
        val date = Date(seconds * 1000L)
        holder.date.text = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(date)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<NotificationResponse>) {
        items.clear()
        items.addAll(newItems.toMutableList())
        notifyDataSetChanged()
    }
}



