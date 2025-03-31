package com.pilapil.sass.model

data class NotificationResponse(
    val subject: String = "",
    val message: String = "",
    val from: String = "",
    val school: String = "",
    val seen: Boolean = false,
    val timestamp: Timestamp? = null
) {
    data class Timestamp(
        val _seconds: Long = 0,
        val _nanoseconds: Long = 0
    )
}
