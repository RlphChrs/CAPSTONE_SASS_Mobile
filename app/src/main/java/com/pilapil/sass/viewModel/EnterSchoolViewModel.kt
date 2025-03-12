package com.pilapil.sass.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pilapil.sass.repository.SchoolRepository

class EnterSchoolViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SchoolRepository(application)

    fun saveSchoolName(schoolName: String) {
        repository.saveSchoolName(schoolName)
    }

    fun getSavedSchoolName(): String? {
        return repository.getSchoolName()
    }
}
