package com.pilapil.sass

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pilapil.sass.adapter.ChatHistoryAdapter
import com.pilapil.sass.adapter.MessageAdapter
import com.pilapil.sass.model.ChatMessage
import com.pilapil.sass.viewModel.ChatViewModel
import com.pilapil.sass.viewModel.EnterSchoolViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var inputMessage: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: MessageAdapter
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var enterSchoolViewModel: EnterSchoolViewModel
    private lateinit var logoImage: ImageView
    private lateinit var termsTextView: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuIcon: ImageView
    private lateinit var navigationView: NavigationView
    private lateinit var chatHistoryRecyclerView: RecyclerView
    private lateinit var chatHistoryLabel: TextView
    private lateinit var chatHistoryAdapter: ChatHistoryAdapter
    private lateinit var chatHistoryList: MutableList<String>

    private val chatMessages = mutableListOf<ChatMessage>() // Store chat messages

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize ViewModels
        enterSchoolViewModel = ViewModelProvider(this)[EnterSchoolViewModel::class.java]
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        // Initialize UI Components
        inputMessage = findViewById(R.id.et_message)
        sendButton = findViewById(R.id.btn_send_message)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        logoImage = findViewById(R.id.logo_image)
        termsTextView = findViewById(R.id.tv_terms_conditions)
        menuIcon = findViewById(R.id.menu_icon)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        chatHistoryRecyclerView = findViewById(R.id.recycler_conversations)
        chatHistoryLabel = findViewById(R.id.chat_history_label)

        // Initialize MessageAdapter
        chatAdapter = MessageAdapter(chatMessages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        // Load chat history from SharedPreferences
        chatHistoryList = getChatHistory().toMutableList()

        // Setup Chat History RecyclerView
        chatHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        chatHistoryAdapter = ChatHistoryAdapter(chatHistoryList) { selectedChat ->
            loadChat(selectedChat)
        }
        chatHistoryRecyclerView.adapter = chatHistoryAdapter

        // Show Chat History if available
        toggleChatHistoryVisibility()

        val schoolName = enterSchoolViewModel.getSavedSchoolName() ?: "Unknown School"

        sendButton.setOnClickListener {
            val userMessage = inputMessage.text.toString()
            if (userMessage.isNotEmpty()) {
                sendMessageToChatbot(schoolName, userMessage)
                hideIntroViews()
                inputMessage.text.clear()
            }
        }

        // 🔹 Open Navigation Drawer when clicking menu icon
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 🔹 Handle Navigation Menu Item Clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_calendar -> openCalendar()
                R.id.nav_notifications -> openNotifications()
                R.id.nav_reports -> openReports()
                R.id.nav_logout -> logoutUser()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Observe Chat Responses
        lifecycleScope.launch {
            chatViewModel.chatResponse.collectLatest { response ->
                response?.let {
                    addMessageToChat(ChatMessage(it, isUser = false))
                }
            }
        }
    }

    private fun sendMessageToChatbot(schoolName: String, userMessage: String) {
        addMessageToChat(ChatMessage(userMessage, isUser = true))
        chatViewModel.sendMessage(schoolName, userMessage)

        // Generate Chat Title (use first few words of message)
        val chatTitle = if (userMessage.length > 30) userMessage.substring(0, 30) + "..." else userMessage

        // Save chat to history
        saveChatHistory(chatTitle)
        updateChatHistory()
    }

    private fun addMessageToChat(chatMessage: ChatMessage) {
        chatMessages.add(chatMessage)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        chatRecyclerView.scrollToPosition(chatMessages.size - 1)
    }

    private fun hideIntroViews() {
        logoImage.visibility = View.GONE
        termsTextView.visibility = View.GONE
    }

    // 🔹 Chat History Management
    private fun saveChatHistory(chatTitle: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("ChatHistory", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve existing chat history
        val chatList = getChatHistory().toMutableList()

        // Prevent duplicates
        if (!chatList.contains(chatTitle)) {
            chatList.add(chatTitle)
        }

        // Save updated list
        val json = Gson().toJson(chatList)
        editor.putString("history", json)
        editor.apply()
    }

    private fun getChatHistory(): List<String> {
        val sharedPreferences: SharedPreferences = getSharedPreferences("ChatHistory", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("history", null)
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }

    private fun updateChatHistory() {
        val newChatHistory = getChatHistory()

        if (chatHistoryList.size < newChatHistory.size) {
            chatHistoryList.add(newChatHistory.last())
            chatHistoryAdapter.notifyItemInserted(chatHistoryList.size - 1)
        } else {
            // Chat was removed (if delete feature is added)
            chatHistoryList.clear()
            chatHistoryList.addAll(newChatHistory)
            chatHistoryAdapter.notifyDataSetChanged()
        }
        toggleChatHistoryVisibility()
    }

    private fun toggleChatHistoryVisibility() {
        if (chatHistoryList.isEmpty()) {
            chatHistoryRecyclerView.visibility = View.GONE
            chatHistoryLabel.visibility = View.GONE
        } else {
            chatHistoryRecyclerView.visibility = View.VISIBLE
            chatHistoryLabel.visibility = View.VISIBLE
        }
    }

    private fun loadChat(chatTitle: String) {
            println("Loading chat: $chatTitle") // log for testing titke
    }


    private fun clearChatHistory() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("ChatHistory", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("history").apply()

        // Clear RecyclerView List
        chatHistoryList.clear()
        chatHistoryAdapter.notifyDataSetChanged()
        toggleChatHistoryVisibility()
    }

    // 🔹 Navigation Item Functions
    private fun openCalendar() {
        // Add logic to open Calendar
    }

    private fun openNotifications() {
        // Add logic to open Notifications
    }

    private fun openReports() {
        // Add logic to open Reports
    }

    private fun logoutUser() {
        // Add logic to handle logout
    }
}
