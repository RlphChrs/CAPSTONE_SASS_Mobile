package com.pilapil.sass.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatHistoryGroup(
    val groupId: String = "",
    val timestamp: Long = 0L,
    val messages: List<ChatMessage> = emptyList()
) : Parcelable
