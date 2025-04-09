package com.pilapil.sass.repository

import com.pilapil.sass.api.ApiService
import com.pilapil.sass.model.ReportRequest
import okhttp3.ResponseBody
import retrofit2.Response

class ReportRepository(private val apiService: ApiService) {
    suspend fun submitReport(token: String, report: ReportRequest): Response<ResponseBody> {
        return apiService.submitReport(report, "Bearer $token")
    }
}
