package com.pilapil.sass.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pilapil.sass.model.ReportRequest
import com.pilapil.sass.repository.ReportRepository
import kotlinx.coroutines.launch
import android.util.Log

class ReportViewModel(private val repository: ReportRepository) : ViewModel() {

    fun submitReport(token: String, report: ReportRequest, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.submitReport(token, report)
                if (response.isSuccessful) {
                    onResult(true)
                } else {
                    Log.e("ReportViewModel", "Failed: ${response.code()}")
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error: ${e.message}")
                onResult(false)
            }
        }
    }
}
