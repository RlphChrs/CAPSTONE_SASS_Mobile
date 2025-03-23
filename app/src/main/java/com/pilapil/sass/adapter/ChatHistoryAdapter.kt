package com.pilapil.sass.adapter

import android.annotation.SuppressLint
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
    private val chatGroups: List<ChatHistoryGroup>,
    private val onChatSelected: (ChatHistoryGroup) -> Unit
) : RecyclerView.Adapter<ChatHistoryAdapter.ChatGroupViewHolder>() {

    inner class ChatGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuestion: TextView = itemView.findViewById(R.id.tv_history_question)
        val tvDate: TextView = itemView.findViewById(R.id.tv_history_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatGroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatGroupViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ChatGroupViewHolder, position: Int) {
        val group = chatGroups[position]

        // Show the first user message
        val firstUserMessage = group.messages.firstOrNull { it.isUser }?.message ?: "No user message"
        holder.tvQuestion.text = firstUserMessage

        // Format timestamp
        val dateText = try {
            val sdf = SimpleDateFormat("MMM dd, yyyy")
            sdf.format(Date(group.timestamp))
        } catch (e: Exception) {
            "Unknown"
        }
        holder.tvDate.text = dateText

        holder.itemView.setOnClickListener {
            onChatSelected(group)
        }
    }

    override fun getItemCount(): Int = chatGroups.size
}
