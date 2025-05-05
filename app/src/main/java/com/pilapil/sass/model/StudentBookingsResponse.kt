package com.pilapil.sass.model

data class StudentBookingsResponse(
    val bookings: List<BookingItem>
)

data class BookingItem(
    val id: String,
    val date: String,
    val fromTime: String,
    val toTime: String,
    val description: String,
    val studentName: String,
    val schoolId: String,
    val createdBy: String
)
