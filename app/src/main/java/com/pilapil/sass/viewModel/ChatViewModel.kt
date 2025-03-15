package com.pilapil.sass.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pilapil.sass.api.PythonApiService
import com.pilapil.sass.model.ChatRequest
import com.pilapil.sass.model.ChatResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val apiService = PythonApiService.create()
    private val _chatResponse = MutableStateFlow<String?>(null)
    val chatResponse: StateFlow<String?> = _chatResponse

    fun sendMessage(schoolName: String, message: String) {
        viewModelScope.launch {
            try {
                val response: ChatResponse = apiService.sendMessage(ChatRequest(schoolName, message))
                _chatResponse.value = response.response
            } catch (e: Exception) {
                _chatResponse.value = "Error: ${e.message}"
            }
        }
    }
}
