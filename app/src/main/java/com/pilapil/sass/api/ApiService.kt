package com.pilapil.sass.api

import com.pilapil.sass.model.ApiResponse
import com.pilapil.sass.model.LoginRequest
import com.pilapil.sass.model.LoginResponse
import com.pilapil.sass.model.Student
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

const val BASE_URL = "http://192.168.1.122:3000/api/"
interface ApiService {

    @POST("/api/students/register/student")
    suspend fun registerStudent(@Body student: Student): ApiResponse

    // Student Login Endpoint
    @POST("/api/students/login/student")
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