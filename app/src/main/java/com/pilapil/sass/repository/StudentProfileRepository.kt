package com.pilapil.sass.repository

import android.util.Log
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.model.StudentProfile
import com.pilapil.sass.model.ApiResponse
import retrofit2.Response

class StudentProfileRepository(private val api: ApiService) {

    suspend fun getProfile(studentId: String, token: String): Response<StudentProfile> {
        return api.getStudentProfile(studentId, token)
    }


    suspend fun updateProfile(token: String, data: Map<String, Any?>): Response<ApiResponse> {
        Log.d("REPOSITORY", "Sending updateProfile to backend")
        Log.d("REPOSITORY", "Token: $token")
        Log.d("REPOSITORY", "Payload: $data")

        val response = api.updateStudentProfile(token, data)

        Log.d("REPOSITORY", "Response Code: ${response.code()}")
        Log.d("REPOSITORY", "Response Body: ${response.body()?.message}")
        return response
    }
}
