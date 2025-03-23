package com.pilapil.sass.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pilapil.sass.api.PythonApiService
import com.pilapil.sass.model.ChatRequest
import com.pilapil.sass.model.ChatResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ChatViewModel : ViewModel() {

    private val apiService = PythonApiService.create()
    private val _chatResponse = MutableStateFlow<String?>(null)
    val chatResponse: StateFlow<String?> = _chatResponse

    fun sendMessage(schoolId: String, studentId: String, message: String) {
        viewModelScope.launch {
            try {
                val chatRequest = ChatRequest(
                    schoolId = schoolId.trim(),
                    studentId = studentId.trim(),
                    userInput = message.trim()
                )

                // ✅ Debugging: Print the request data
                println("🔍 Sending request to chatbot: $chatRequest")

                val response: ChatResponse = apiService.sendMessage(chatRequest)

                // ✅ Debugging: Print chatbot response
                println("🔍 Chatbot Response: ${response.botResponse}")

                _chatResponse.value = response.botResponse
            } catch (e: HttpException) {
                println("❌ Chatbot HTTP Exception: ${e.response()?.errorBody()?.string()}")
                _chatResponse.value = "Error: Chatbot failed to respond."
            } catch (e: Exception) {
                println("❌ Unexpected Chatbot Error: ${e.message}")
                _chatResponse.value = "Error: ${e.message}"
            }
        }
    }

}
