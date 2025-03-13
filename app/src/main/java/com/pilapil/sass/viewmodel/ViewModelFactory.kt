package com.pilapil.sass.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pilapil.sass.repository.StudentAuthRepository

class ViewModelFactory(private val repository: StudentAuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentAuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudentAuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
