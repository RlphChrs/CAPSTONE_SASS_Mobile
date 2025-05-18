package com.pilapil.sass.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.pilapil.sass.model.StudentProfile
import com.pilapil.sass.repository.StudentProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: StudentProfileRepository) : ViewModel() {

    private val _studentProfile = MutableLiveData<StudentProfile?>()
    val studentProfile: LiveData<StudentProfile?> = _studentProfile

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: LiveData<Boolean> = _updateResult

    fun fetchProfile(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getProfile(token)
                if (response.isSuccessful) {
                    _studentProfile.postValue(response.body())
                } else {
                    Log.e("VIEWMODEL", "❌ HTTP ${response.code()}")
                    _studentProfile.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("VIEWMODEL", "❌ Exception: ${e.message}")
                _studentProfile.postValue(null)
            }
        }
    }


    private fun extractStudentIdFromToken(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT)
            val json = String(payload)
            val regex = Regex("\"id\"\\s*:\\s*\"(.*?)\"")
            val match = regex.find(json)
            match?.groups?.get(1)?.value
        } catch (e: Exception) {
            Log.e("VIEWMODEL", "❌ Failed to extract studentId from token: ${e.localizedMessage}")
            null
        }
    }

    fun updateProfile(token: String, data: Map<String, Any?>) {
        viewModelScope.launch {
            try {
                val response = repository.updateProfile(token, data)
                Log.d("VIEWMODEL", "Update Response Code: ${response.code()}")
                Log.d("VIEWMODEL", "Update Success: ${response.isSuccessful}")
                Log.d("VIEWMODEL", "Response Body: ${response.body()}")

                _updateResult.postValue(response.isSuccessful)

                // ✅ Re-fetch profile if updated
                if (response.isSuccessful) {
                    fetchProfile(token)
                }

            } catch (e: Exception) {
                Log.e("VIEWMODEL", "❌ Update failed: ${e.localizedMessage}")
                _updateResult.postValue(false)
            }
        }
    }

}
