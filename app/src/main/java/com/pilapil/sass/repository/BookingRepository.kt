package com.pilapil.sass.repository

import com.pilapil.sass.api.ApiService
import com.pilapil.sass.model.BookingRequest
import com.pilapil.sass.model.BookingResponse
import retrofit2.Response

class BookingRepository(private val api: ApiService) {

    suspend fun bookAppointment(token: String, request: BookingRequest): Response<BookingResponse> {
        return api.bookAppointment("Bearer $token", request)
    }

    suspend fun getAvailableSlots(token: String, schoolId: String, date: String) =
        api.getAvailableTimeSlots(schoolId, date, "Bearer $token")

    suspend fun getStudentBookings(token: String, studentId: String) =
        api.getStudentBookings(studentId, "Bearer $token")
}
