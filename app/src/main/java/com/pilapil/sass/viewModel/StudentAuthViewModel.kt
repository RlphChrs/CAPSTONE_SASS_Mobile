package com.pilapil.sass.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.model.ChatMessage
import com.pilapil.sass.model.ChatSaveRequest
import com.pilapil.sass.model.Student
import com.pilapil.sass.repository.StudentAuthRepository
import com.pilapil.sass.view.LoginActivity
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
        context: Context,
        email: String,
        password: String,
        onSuccess: (String, String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val (token, studentId, schoolName) = repository.loginStudent(email, password)
                onSuccess(token, studentId, schoolName)

                FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
                    Log.d("✅ FCM", "Token: $fcmToken")

                    viewModelScope.launch {
                        try {
                            val response = apiService.updateFcmToken(
                                token = "Bearer $token",
                                fcmToken = mapOf("fcmToken" to fcmToken)
                            )
                            if (response.isSuccessful) {
                                Log.d("✅ FCM", "Token saved successfully.")
                            } else {
                                Log.e("❌ FCM", "Failed to save token: ${response.code()}")
                            }
                        } catch (e: Exception) {
                            Log.e("❌ FCM", "Exception while saving token: ${e.message}")
                        }
                    }
                }.addOnFailureListener {
                    Log.e("❌ FCM", "Failed to get FCM token")
                }

            } catch (e: HttpException) {
                onError(e.response()?.errorBody()?.string() ?: "Login failed")
            } catch (e: Exception) {
                onError(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun saveChatGroup(
        token: String,
        schoolId: String,
        studentId: String,
        groupId: String,
        messages: List<ChatMessage>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        println("📥 Entered saveChatGroup in ViewModel")

        viewModelScope.launch {
            try {
                println("📤 ViewModel is calling repository.saveChat()...")
                val request = ChatSaveRequest(
                    studentId = studentId,
                    groupId = groupId,
                    messages = messages
                )
                println("📡 Sending request: $request")
                repository.saveChat(token, schoolId, studentId, groupId, messages)
                println("✅ Successfully saved chat group!")
                onSuccess()
            } catch (e: Exception) {
                println("❌ Exception in saveChatGroup: ${e.message}")
                e.printStackTrace() // 🔥 add this
                onError(e.message ?: "Failed to save group chat.")
            }
        }
    }

    fun saveFcmTokenToBackend(token: String, fcmToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.updateFcmToken(
                    token = "Bearer $token",
                    fcmToken = mapOf("fcmToken" to fcmToken)
                )
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Failed with code ${response.code()}")
                }
            } catch (e: Exception) {
                onError("Exception: ${e.message}")
            }
        }
    }

}
