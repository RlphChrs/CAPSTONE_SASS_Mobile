package com.pilapil.sass.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pilapil.sass.R
import com.pilapil.sass.adapter.ChatHistoryAdapter
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.model.ChatHistoryGroup
import com.pilapil.sass.utils.SessionManager
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatHistoryAdapter
    private val chatGroups = mutableListOf<ChatHistoryGroup>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_history, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_history)
        adapter = ChatHistoryAdapter(chatGroups) {
            // Optional: handle on group click
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val sessionManager = SessionManager(requireContext())
        val studentId = sessionManager.getStudentId() ?: return
        val token = "Bearer ${sessionManager.getAuthToken()}"

        lifecycleScope.launch {
            try {
                val response = ApiService.create().getChatHistory(studentId, token)
                if (response.isSuccessful) {
                    val conversations = response.body()?.conversations ?: emptyList()
                    chatGroups.clear()
                    chatGroups.addAll(conversations)
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("HistoryFragment", "❌ Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("HistoryFragment", "❌ Exception: ${e.message}", e)
            }
        }
    }
}
