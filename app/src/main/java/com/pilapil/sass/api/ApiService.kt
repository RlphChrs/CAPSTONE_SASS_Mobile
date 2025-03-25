package com.pilapil.sass.api

import com.pilapil.sass.model.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

//const val BASE_URL = "http://192.168.1.122:3000/api/"
const val BASE_URL = "http://192.168.1.52:3000/api/" // Office IP
//const val PYTHON_BASE_URL = "http://192.168.1.122:8000/"
const val PYTHON_BASE_URL = "http://192.168.1.52:8000/" // Office Chatbot Backend

interface ApiService {
    @POST("students/register/student")
    suspend fun registerStudent(@Body student: Student): ApiResponse

    @POST("students/login")
    suspend fun loginStudent(@Body loginRequest: LoginRequest): LoginResponse

    @POST("students/chat/save")
    suspend fun saveChat(
        @Header("Authorization") token: String,
        @Body chatRequest: ChatSaveRequest
    ): ApiResponse

    @GET("students/chat/history/{studentId}")
    suspend fun getChatHistory(
        @Path("studentId") studentId: String,
        @Header("Authorization") token: String
    ): Response<ChatHistoryResponse>


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
    @POST("chatbot")
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
