package com.pilapil.sass.model

data class ChatHistoryGroup(
    val groupId: String = "",
    val timestamp: Long = 0L,
    val messages: List<ChatMessage> = emptyList()
)
