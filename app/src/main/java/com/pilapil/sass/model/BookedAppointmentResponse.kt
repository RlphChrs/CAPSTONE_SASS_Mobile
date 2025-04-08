package com.pilapil.sass.model

data class BookedAppointmentsResponse(
    val bookings: List<BookedAppointment>
)

data class BookedAppointment(
    val id: String,
    val studentId: String,
    val studentName: String,
    val fromTime: String,
    val toTime: String,
    val description: String,
    val date: String,
    val schoolId: String,
    val createdBy: String
)
