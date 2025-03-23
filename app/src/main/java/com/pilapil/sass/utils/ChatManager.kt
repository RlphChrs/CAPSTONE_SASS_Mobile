package com.pilapil.sass.utils

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import com.pilapil.sass.api.PythonApiService
import com.pilapil.sass.model.ChatMessage
import com.pilapil.sass.model.ChatRequest
import com.pilapil.sass.model.ChatResponse
import com.pilapil.sass.viewModel.StudentAuthViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ChatManager(
    private val context: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val viewModel: StudentAuthViewModel,
    private val apiService: PythonApiService
) {

    private val messageBuffer = mutableListOf<ChatMessage>()
    fun sendMessageToChatbot(
        schoolId: String,
        studentId: String,
        userMessage: String,
        onMessageReceived: (ChatMessage) -> Unit,
        onError: (String) -> Unit
    ) {
        val userChatMessage = ChatMessage(userMessage, isUser = true)
        onMessageReceived(userChatMessage)
        messageBuffer.add(userChatMessage)

        lifecycleScope.launch {
            try {
                val chatRequest = ChatRequest(schoolId, studentId, userMessage)
                val response: ChatResponse = apiService.sendMessage(chatRequest)

                val botResponseText = response.botResponse ?: "No response from chatbot"
                val botMessage = ChatMessage(botResponseText, isUser = false)
                onMessageReceived(botMessage)
                messageBuffer.add(botMessage) // Add bot response to buffer
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    fun saveBufferedMessages() {
        val sessionManager = SessionManager(context)
        val rawToken = sessionManager.getAuthToken()
        val token =
            if (rawToken != null && rawToken.startsWith("Bearer")) rawToken else "Bearer $rawToken"
        val studentId = sessionManager.getStudentId()
        val schoolId = sessionManager.getSchoolId()

        if (token == "Bearer null" || studentId == null || schoolId == null || messageBuffer.isEmpty()) {
            Toast.makeText(context, "Auth error or empty chat.", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            viewModel.saveChatGroup(
                token = token,
                schoolId = schoolId,
                studentId = studentId,
                messages = messageBuffer.toList(),
                onSuccess = {
                    println("✅ Chat group saved.")
                    messageBuffer.clear()
                },
                onError = {
                    Toast.makeText(context, "❌ Failed to save chat group", Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }

    }
}
