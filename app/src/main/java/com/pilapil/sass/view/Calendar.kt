package com.pilapil.sass.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.pilapil.sass.R

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var bookButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar2, container, false)

        // Initialize Views
        calendarView = view.findViewById(R.id.calendarView)
        bookButton = view.findViewById(R.id.btn_book_appointment)

        // Handle Calendar Date Selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            Toast.makeText(requireContext(), "Selected Date: $selectedDate", Toast.LENGTH_SHORT).show()
        }

        // Handle Button Click
        bookButton.setOnClickListener {
            Toast.makeText(requireContext(), "Appointment Booked!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
