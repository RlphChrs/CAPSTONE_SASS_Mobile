package com.pilapil.sass.model

data class ChatHistoryResponse(
    val conversations: List<ChatHistoryGroup> = emptyList()
)
