package com.pilapil.sass.model

data class ChatMessage(
    val message: String,
    val isUser: Boolean // ✅ true = User, false = Chatbot
)
