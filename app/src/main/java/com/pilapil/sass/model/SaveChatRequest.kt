package com.pilapil.sass.model

data class ChatSaveRequest(
    val studentId: String,
    val groupId: String,
    val messages: List<ChatMessage>
)
