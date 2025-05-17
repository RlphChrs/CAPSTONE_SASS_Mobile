package com.pilapil.sass.model
data class ChatRequest(
    val schoolId: String,
    val studentId: String,
    val userInput: String,
    val chat_history: List<ChatApiMessage>
)


