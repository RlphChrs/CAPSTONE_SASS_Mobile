package com.pilapil.sass.model

data class ChatHistoryMessage(
    val message: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
