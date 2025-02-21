package com.pilapil.sass.repository

import com.pilapil.sass.api.ApiService
import com.pilapil.sass.model.LoginRequest
import com.pilapil.sass.model.Student
import retrofit2.HttpException

class StudentAuthRepository (private val apiService: ApiService) {
    suspend fun registerStudent(student: Student): String {
        return try {
            val response = apiService.registerStudent(student)
            response.message // Returns "Registration successful"
        } catch (e: HttpException) {
            throw Exception("Registration failed: ${e.response()?.errorBody()?.string()}")
        }
    }

    suspend fun loginStudent(email: String, password: String): String {
        return try {
            val response = apiService.loginStudent(LoginRequest(email, password))
            response.token // Returns JWT token
        } catch (e: HttpException) {
            throw Exception("Login failed: ${e.response()?.errorBody()?.string()}")
        }
    }
}