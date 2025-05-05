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
import java.io.IOException
import java.net.SocketTimeoutException

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
                messageBuffer.add(botMessage)
            } catch (e: SocketTimeoutException) {
                onError("The chatbot is taking too long to respond. Please try again.")
            } catch (e: IOException) {
                onError("Network error. Please check your internet connection.")
            } catch (e: HttpException) {
                onError("Server error: ${e.message}")
            } catch (e: Exception) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun saveBufferedMessages() {
        println("🧪 Message buffer before saving: $messageBuffer")

        val sessionManager = SessionManager(context)
        val rawToken = sessionManager.getAuthToken()
        val token = if (rawToken != null && rawToken.startsWith("Bearer")) rawToken else "Bearer $rawToken"
        val studentId = sessionManager.getStudentId()
        val schoolId = sessionManager.getSchoolId()

        println("🪪 Auth Details -> token: $token, studentId: $studentId, schoolId: $schoolId")

        if (token == "Bearer null" || studentId == null || schoolId == null || messageBuffer.isEmpty()) {
            println("❌ Missing token or student/school ID or messages. Aborting save.")
            return
        }

        val groupId = "grp_${System.currentTimeMillis()}"
        println("🆔 Generated Group ID: $groupId")
        println("📨 About to call viewModel.saveChatGroup()...")

        kotlinx.coroutines.GlobalScope.launch {
            try {
                println("📥 Entered saveChatGroup in ViewModel (via GlobalScope)")
                viewModel.saveChatGroup(
                    token = token,
                    schoolId = schoolId,
                    studentId = studentId,
                    groupId = groupId,
                    messages = messageBuffer.toList(),
                    onSuccess = {
                        println("✅ Chat group saved to backend")
                        messageBuffer.clear()
                    },
                    onError = {
                        println("❌ Error saving chat: $it")
                    }
                )
            } catch (e: Exception) {
                println("🔥 EXCEPTION in GlobalScope saveChatGroup: ${e.message}")
                e.printStackTrace()
            }
        }
    }

}
