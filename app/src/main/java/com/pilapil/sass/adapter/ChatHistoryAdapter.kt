package com.pilapil.sass.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pilapil.sass.R
import com.pilapil.sass.model.ChatHistoryGroup
import java.text.SimpleDateFormat
import java.util.*

class ChatHistoryAdapter(
    private val groups: List<ChatHistoryGroup>,
    private val onClick: (ChatHistoryGroup) -> Unit
) : RecyclerView.Adapter<ChatHistoryAdapter.ChatHistoryViewHolder>() {

    inner class ChatHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_history_name)

        fun bind(group: ChatHistoryGroup) {
            val firstUserMessage = group.messages.firstOrNull { it.isUser }?.message
            val previewText = firstUserMessage ?: "Chat ID: ${group.groupId.takeLast(4)}"
            title.text = previewText

            itemView.setOnClickListener {
                onClick(group)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ChatHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatHistoryViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount(): Int = groups.size
}

