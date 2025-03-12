package com.pilapil.sass.api

import com.pilapil.sass.model.ApiResponse
import com.pilapil.sass.model.ChatRequest
import com.pilapil.sass.model.ChatResponse
import com.pilapil.sass.model.LoginRequest
import com.pilapil.sass.model.LoginResponse
import com.pilapil.sass.model.Student
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

const val BASE_URL = "http://192.168.1.122:3000/api/"
const val PYTHON_BASE_URL = "http://192.168.1.122:8000/" // chatbot Backend
interface ApiService {

    @POST("/api/students/register/student")
    suspend fun registerStudent(@Body student: Student): ApiResponse

    // Student Login Endpoint
    @POST("/api/students/login")
    suspend fun loginStudent(@Body loginRequest: LoginRequest): LoginResponse

    companion object {
        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}

interface PythonApiService {
    @POST("/chatbot")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse

    companion object {
        fun create(): PythonApiService {
            return Retrofit.Builder()
                .baseUrl(PYTHON_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PythonApiService::class.java)
        }
    }
}