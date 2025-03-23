package com.pilapil.sass.model
data class ChatRequest(
    val schoolId: String,   // ✅ Use camelCase to match backend
    val studentId: String,  // ✅ Consistently use studentId
    val userInput: String   // ✅ Consistently use userInput
)


