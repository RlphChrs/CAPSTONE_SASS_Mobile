package com.pilapil.sass

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pilapil.sass.adapter.MessageAdapter
import com.pilapil.sass.model.ChatMessage
import com.pilapil.sass.viewmodel.ChatViewModel
import com.pilapil.sass.viewmodel.EnterSchoolViewModel
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

    private val chatMessages = mutableListOf<ChatMessage>() // Store chat messages

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // initializing viewModels manually since view mnodel dependency is not working
        enterSchoolViewModel = ViewModelProvider(this)[EnterSchoolViewModel::class.java]
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        inputMessage = findViewById(R.id.et_message)
        sendButton = findViewById(R.id.btn_send_message)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        logoImage = findViewById(R.id.logo_image)
        termsTextView = findViewById(R.id.tv_terms_conditions)

        // Initialize MessageAdapter with the cha list
        chatAdapter = MessageAdapter(chatMessages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        val schoolName = enterSchoolViewModel.getSavedSchoolName() ?: "Unknown School"

        sendButton.setOnClickListener {
            val userMessage = inputMessage.text.toString()
            if (userMessage.isNotEmpty()) {
                sendMessageToChatbot(schoolName, userMessage)
                hideIntroViews() // 🔹 Hide logo and terms on first message
                inputMessage.text.clear()
            }
        }

        // 🔹 Observe Chat Responses from ViewModel
        lifecycleScope.launch {
            chatViewModel.chatResponse.collectLatest { response ->
                response?.let {
                    addMessageToChat(ChatMessage(it, isUser = false)) // Display bot response
                }
            }
        }
    }

    private fun sendMessageToChatbot(schoolName: String, userMessage: String) {
        addMessageToChat(ChatMessage(userMessage, isUser = true))
        chatViewModel.sendMessage(schoolName, userMessage)
    }

    private fun addMessageToChat(chatMessage: ChatMessage) {
        chatMessages.add(chatMessage) // Add message to list
        chatAdapter.notifyItemInserted(chatMessages.size - 1) // Notify RecyclerView
        chatRecyclerView.scrollToPosition(chatMessages.size - 1) // Scroll to bottom
    }

    private fun hideIntroViews() {
        logoImage.visibility = View.GONE
        termsTextView.visibility = View.GONE
    }
}
