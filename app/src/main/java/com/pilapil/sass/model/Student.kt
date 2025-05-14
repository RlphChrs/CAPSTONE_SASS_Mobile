package com.pilapil.sass.model

data class Student(
    val studentId: String,
    val email: String,
    val password: String,
    val repeatPassword: String,
    val firstName: String = "",  // now optional / ignored
    val lastName: String = "",   // now optional / ignored
    val schoolName: String,
    val role: String = "Student",
    val createdAt: String,
    val termsAccepted: Boolean
)


data class LoginRequest(
    val email: String,
    val password: String
)

data class ApiResponse(
    val message: String,
    val schoolName: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val studentId: String,
    val schoolName: String
)

