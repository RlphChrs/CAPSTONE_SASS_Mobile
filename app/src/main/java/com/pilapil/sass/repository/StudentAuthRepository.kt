package com.pilapil.sass.repository

import com.pilapil.sass.api.ApiService
import com.pilapil.sass.model.ChatHistoryGroup
import com.pilapil.sass.model.ChatMessage
import com.pilapil.sass.model.ChatHistoryResponse
import com.pilapil.sass.model.ChatSaveRequest
import com.pilapil.sass.model.Student
import com.pilapil.sass.model.LoginRequest
import retrofit2.HttpException

class StudentAuthRepository(private val apiService: ApiService) {

    suspend fun registerStudent(student: Student): Pair<String, String> {
        return try {
            val response = apiService.registerStudent(student)
            Pair(response.message, response.schoolName)
        } catch (e: HttpException) {
            throw Exception(
                "Registration failed: ${
                    e.response()?.errorBody()?.string() ?: "Unknown error"
                }"
            )
        }
    }

    suspend fun loginStudent(email: String, password: String): Triple<String, String, String> {
        return try {
            val response = apiService.loginStudent(LoginRequest(email, password))
            Triple(
                response.token,
                response.studentId,
                response.schoolName
            )
        } catch (e: HttpException) {
            throw Exception(
                "Login failed: ${
                    e.response()?.errorBody()?.string() ?: "Unknown error"
                }"
            )
        }
    }

    suspend fun saveChat(
        token: String,
        schoolId: String,
        studentId: String,
        groupId: String,
        messages: List<ChatMessage>
    ) {
        try {
            val chatRequest = ChatSaveRequest(
                studentId = studentId,
                groupId = groupId,
                messages = messages
            )
            println("📦 Repository sending chat request: $chatRequest")
            println("🔐 With token: $token")
            val response = apiService.saveChat(token, chatRequest)
            println("✅ Chat save response: ${response.message}")
        } catch (e: Exception) {
            println("❌ Error in repository.saveChat: ${e.message}")
            e.printStackTrace() // 🔥 see the full stacktrace
            throw Exception("Failed to save chat: ${e.message}")
        }
    }



    suspend fun getChatHistory(token: String, studentId: String): List<ChatHistoryGroup> {
        val response = apiService.getChatHistory(studentId, token)
        if (response.isSuccessful) {
            return response.body()?.conversations ?: emptyList()
        } else {
            throw Exception("Failed to fetch chat history: ${response.errorBody()?.string()}")
        }
    }
}

