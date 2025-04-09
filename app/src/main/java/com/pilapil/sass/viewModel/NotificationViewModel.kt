package com.pilapil.sass.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.model.NotificationResponse
import com.pilapil.sass.utils.SessionManager
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val _notifications = MutableLiveData<List<NotificationResponse>>()
    val notifications: LiveData<List<NotificationResponse>> = _notifications

    private val apiService = ApiService.create()

    fun fetchNotifications(context: Context) {
        val token = SessionManager(context).getAuthToken()
        val bearerToken = "Bearer $token"

        viewModelScope.launch {
            try {
                val response = apiService.getStudentNotifications(bearerToken)
                if (response.isSuccessful) {
                    _notifications.value = response.body()?.responses ?: emptyList()
                    Log.d("NotificationViewModel", "✅ Fetched ${_notifications.value?.size} notifications")
                } else {
                    Log.e("NotificationViewModel", "❌ Error ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "❗ Exception: ${e.localizedMessage}", e)
            }
        }
    }
}
