package com.pilapil.sass.model

data class Student(
    val userId: String,
    val email: String,
    val password: String,
    val repeatPassword: String,  // ✅ Add repeatPassword
    val firstName: String,
    val lastName: String,
    val role: String = "Student",
    val createdAt: String,
    val termsAccepted: Boolean
)


data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val token: String
)

data class ApiResponse(
    val message: String
)
