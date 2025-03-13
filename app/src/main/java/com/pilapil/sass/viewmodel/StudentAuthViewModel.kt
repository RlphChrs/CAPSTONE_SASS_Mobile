package com.pilapil.sass.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pilapil.sass.repository.StudentAuthRepository
import com.pilapil.sass.model.Student
import kotlinx.coroutines.launch

class StudentAuthViewModel(private val repository: StudentAuthRepository) : ViewModel() {

    fun registerStudent(student: Student, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val message = repository.registerStudent(student)
                onSuccess(message)
            } catch (e: Exception) {
                onError(e.message ?: "Registration failed")
            }
        }
    }

    fun loginStudent(email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val token = repository.loginStudent(email, password)
                onSuccess(token)
            } catch (e: Exception) {
                onError(e.message ?: "Login failed")
            }
        }
    }
}
