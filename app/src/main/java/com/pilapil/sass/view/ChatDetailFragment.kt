package com.pilapil.sass.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pilapil.sass.R
import com.pilapil.sass.adapter.MessageAdapter
import com.pilapil.sass.model.ChatHistoryGroup
import com.pilapil.sass.model.ChatMessage

class ChatDetailFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_chat_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_messages)
        adapter = MessageAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val group = arguments?.getParcelable<ChatHistoryGroup>("chatGroup")

        if (group != null) {
            messages.clear()
            messages.addAll(group.messages)
            adapter.notifyDataSetChanged()

            Log.d("ChatDetailFragment", "📨 Loaded ${group.messages.size} messages from group: ${group.groupId}")
        } else {
            Toast.makeText(requireContext(), "⚠️ Failed to load conversation", Toast.LENGTH_SHORT).show()
        }
    }
}
