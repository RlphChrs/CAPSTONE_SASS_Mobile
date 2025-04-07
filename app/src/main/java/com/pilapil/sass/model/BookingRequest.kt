package com.pilapil.sass.model

data class BookingRequest(
    val date: String,
    val fromTime: String,
    val toTime: String,
    val description: String
)
