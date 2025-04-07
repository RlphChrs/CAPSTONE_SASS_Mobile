package com.pilapil.sass.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pilapil.sass.R
import com.pilapil.sass.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import java.util.Locale



class Calendar : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Default to today's date
        selectedDate = dateFormat.format(Date())

        // When a date is selected from CalendarView
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = dateFormat.format(calendar.time)

            // Navigate to ScheduleFragment
            val fragment = ScheduleFragment()
            val bundle = Bundle()
            bundle.putString("SELECTED_DATE", selectedDate)
            fragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        // When the Book Appointment button is clicked
        binding.btnBookAppointment.setOnClickListener {
            val bookingFragment = AppointmentBookingFragment()
            val bundle = Bundle()
            bundle.putString("SELECTED_DATE", selectedDate)
            bookingFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, bookingFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
