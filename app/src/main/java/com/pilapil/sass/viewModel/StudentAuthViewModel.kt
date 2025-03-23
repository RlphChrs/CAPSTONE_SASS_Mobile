package com.pilapil.sass.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.model.ChatHistoryGroup
import com.pilapil.sass.model.ChatMessage
import com.pilapil.sass.model.ChatRequest
import com.pilapil.sass.model.ChatSaveRequest
import com.pilapil.sass.model.Student
import com.pilapil.sass.repository.StudentAuthRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class StudentAuthViewModel(private val repository: StudentAuthRepository) : ViewModel() {

    private val apiService: ApiService = ApiService.create()

    fun registerStudent(
        student: Student,
        onSuccess: (String, String) -> Unit, // message, schoolName
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val (message, schoolName) = repository.registerStudent(student)
                onSuccess(message, schoolName)
            } catch (e: Exception) {
                onError(e.message ?: "Registration failed")
            }
        }
    }

    fun loginStudent(
        email: String, password: String,
        onSuccess: (String, String, String) -> Unit, // token, studentId, schoolName
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val (token, studentId, schoolName) = repository.loginStudent(email, password)
                onSuccess(token, studentId, schoolName)
            } catch (e: HttpException) {
                onError(e.response()?.errorBody()?.string() ?: "Login failed")
            } catch (e: Exception) {
                onError(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun saveChatGroup(
        token: String, schoolId: String, studentId: String, messages: List<ChatMessage>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = ChatSaveRequest(studentId = studentId, messages = messages)
                apiService.saveChat(token, request)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to save group chat.")
            }
        }
    }
}
