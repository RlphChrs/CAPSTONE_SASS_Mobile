package com.pilapil.sass.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pilapil.sass.repository.StudentProfileRepository

class ProfileViewModelFactory(private val repository: StudentProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(repository) as T
    }
}
