package com.pilapil.sass.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pilapil.sass.R

class ChatHistoryAdapter(
    private val chatHistory: List<String>,
    private val onChatSelected: (String) -> Unit
) : RecyclerView.Adapter<ChatHistoryAdapter.ChatHistoryViewHolder>() {

    inner class ChatHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatTitle: TextView = itemView.findViewById(R.id.chat_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatHistoryViewHolder, position: Int) {
        val chatTitle = chatHistory[position]
        holder.chatTitle.text = chatTitle

        holder.itemView.setOnClickListener {
            onChatSelected(chatTitle)
        }
    }

    override fun getItemCount(): Int = chatHistory.size
}
