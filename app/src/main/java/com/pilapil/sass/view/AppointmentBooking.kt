package com.pilapil.sass.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pilapil.sass.R
import androidx.appcompat.app.AppCompatActivity

class AppointmentBooking : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_appointment_booking) // Ensure this layout exists
    }
}