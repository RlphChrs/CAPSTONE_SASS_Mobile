package com.pilapil.sass.viewmodel

import androidx.lifecycle.*
import com.pilapil.sass.model.AvailableTimeResponse
import com.pilapil.sass.repository.BookingRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class BookingViewModel(private val repository: BookingRepository) : ViewModel() {

    private val _availableSlots = MutableLiveData<Response<AvailableTimeResponse>>()
    val availableSlots: LiveData<Response<AvailableTimeResponse>> = _availableSlots

    fun getAvailableSlots(token: String, schoolId: String, date: String) {
        viewModelScope.launch {
            val response = repository.getAvailableSlots(token, schoolId, date)
            _availableSlots.postValue(response)
        }
    }

}
