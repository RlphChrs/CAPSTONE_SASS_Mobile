package com.pilapil.sass.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.pilapil.sass.R
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.databinding.FragmentAppointmentBookingBinding
import com.pilapil.sass.model.BookingRequest
import com.pilapil.sass.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import java.util.Date

class AppointmentBookingFragment : Fragment() {

    private var _binding: FragmentAppointmentBookingBinding? = null
    private val binding get() = _binding!!
    private var selectedDate: String = ""
    private var selectedTime: String? = null
    private lateinit var sessionManager: SessionManager
    private val apiService = ApiService.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentBookingBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selectedDate = arguments?.getString("SELECTED_DATE") ?: getTodayDate()
        selectedTime = arguments?.getString("SELECTED_TIME")

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = formatter.parse(selectedDate)!!
        binding.calendarView.date = calendar.timeInMillis

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = formatter.format(calendar.time)
        }

        setupTimePickers()

        if (!selectedTime.isNullOrEmpty()) {
            // Auto-fill time
            val hour = selectedTime!!.split(":")[0].toInt()
            val amPm = if (hour >= 12) "PM" else "AM"
            val formattedHour = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
            val hourStr = String.format("%02d", formattedHour)

            val fromIndex = (1..12).map { String.format("%02d", it) }.indexOf(hourStr)
            val toIndex = if (fromIndex + 1 < 12) fromIndex + 1 else 0

            binding.fromHourSpinner.setSelection(fromIndex)
            binding.toHourSpinner.setSelection(toIndex)

            if (amPm == "AM") {
                binding.buttonAMFrom.isChecked = true
                binding.buttonAMTo.isChecked = true
            } else {
                binding.buttonPMFrom.isChecked = true
                binding.buttonPMTo.isChecked = true
            }

            // Disable controls
            binding.fromHourSpinner.isEnabled = false
            binding.toHourSpinner.isEnabled = false
            binding.toggleAmPmFrom.isEnabled = false
            binding.toggleAmPmTo.isEnabled = false
            binding.calendarView.isEnabled = false
        } else {
            // Manual input mode
            binding.fromHourSpinner.isEnabled = true
            binding.toHourSpinner.isEnabled = true
            binding.toggleAmPmFrom.isEnabled = true
            binding.toggleAmPmTo.isEnabled = true
            binding.calendarView.isEnabled = true
        }

        binding.btnConfirmSelection.setOnClickListener {
            val description = binding.etDescription.text.toString().trim()
            val fromHour = binding.fromHourSpinner.selectedItem.toString()
            val toHour = binding.toHourSpinner.selectedItem.toString()
            val fromPeriod = if (binding.buttonAMFrom.isChecked) "AM" else "PM"
            val toPeriod = if (binding.buttonAMTo.isChecked) "AM" else "PM"

            val fromTime = convertTo24Hour(fromHour, fromPeriod)
            val toTime = convertTo24Hour(toHour, toPeriod)
            val fullDateTime = "$selectedDate, $fromTime - $toTime"

            showConfirmationDialog(description, fullDateTime, fromTime, toTime)
        }
    }

    private fun setupTimePickers() {
        val hours = (1..12).map { String.format("%02d", it) }
        val hourAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, hours)

        binding.fromHourSpinner.adapter = hourAdapter
        binding.toHourSpinner.adapter = hourAdapter

        binding.fromHourSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                binding.fromHourLabel.text = hours[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.toHourSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                binding.toHourLabel.text = hours[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.toggleAmPmFrom.setOnCheckedChangeListener { _, checkedId ->
            val isAM = checkedId == R.id.buttonAM_from
            binding.buttonAMFrom.isChecked = isAM
            binding.buttonPMFrom.isChecked = !isAM
            binding.buttonAMFrom.setBackgroundResource(if (isAM) R.drawable.login_signin else R.drawable.white_bg)
            binding.buttonAMFrom.setTextColor(resources.getColor(if (isAM) R.color.white else R.color.black))
            binding.buttonPMFrom.setBackgroundResource(if (!isAM) R.drawable.login_signin else R.drawable.white_bg)
            binding.buttonPMFrom.setTextColor(resources.getColor(if (!isAM) R.color.white else R.color.black))
        }

        binding.toggleAmPmTo.setOnCheckedChangeListener { _, checkedId ->
            val isAM = checkedId == R.id.buttonAM_to
            binding.buttonAMTo.isChecked = isAM
            binding.buttonPMTo.isChecked = !isAM
            binding.buttonAMTo.setBackgroundResource(if (isAM) R.drawable.login_signin else R.drawable.white_bg)
            binding.buttonAMTo.setTextColor(resources.getColor(if (isAM) R.color.white else R.color.black))
            binding.buttonPMTo.setBackgroundResource(if (!isAM) R.drawable.login_signin else R.drawable.white_bg)
            binding.buttonPMTo.setTextColor(resources.getColor(if (!isAM) R.color.white else R.color.black))
        }
    }

    private fun showConfirmationDialog(description: String, dateTime: String, fromTime: String, toTime: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialogue_booking, null)

        val tvDescription = dialogView.findViewById<TextView>(R.id.tv_dialog_description)
        val tvDateTime = dialogView.findViewById<TextView>(R.id.tv_dialog_datetime)
        val btnBook = dialogView.findViewById<TextView>(R.id.btn_dialog_book)

        tvDescription.text = description
        tvDateTime.text = dateTime

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnBook.setOnClickListener {
            dialog.dismiss()
            submitAppointment(fromTime, toTime, selectedDate, description)
        }

        dialog.show()
    }

    private fun submitAppointment(fromTime: String, toTime: String, date: String, description: String) {
        val token = sessionManager.getAuthToken()
        val request = BookingRequest(
            fromTime = fromTime,
            toTime = toTime,
            date = date,
            description = description
        )

        Log.d("BOOKING_DEBUG", "Token: Bearer $token")
        Log.d("BOOKING_DEBUG", "BookingRequest: $request")

        lifecycleScope.launch {
            try {
                val response = apiService.bookAppointment("Bearer $token", request)
                if (response.isSuccessful) {
                    showSuccessDialog()
                } else {
                    Toast.makeText(requireContext(), response.errorBody()?.string() ?: "Booking failed.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("BOOKING_ERROR", "Booking failed: ${e.message}", e)
            }
        }
    }

    private fun showSuccessDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_success_booking, null)
        val btnOk = dialogView.findViewById<TextView>(R.id.btn_ok)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnOk.setOnClickListener {
            dialog.dismiss()
            resetForm()
        }

        dialog.show()
    }

    private fun resetForm() {
        binding.etDescription.setText("")

        val today = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = formatter.format(today.time)
        binding.calendarView.date = today.timeInMillis

        binding.fromHourSpinner.setSelection(0)
        binding.toHourSpinner.setSelection(0)

        binding.buttonAMFrom.isChecked = true
        binding.buttonPMFrom.isChecked = false
        binding.buttonAMTo.isChecked = true
        binding.buttonPMTo.isChecked = false

        binding.fromHourLabel.text = "01"
        binding.toHourLabel.text = "01"

        // Re-enable controls after reset
        binding.fromHourSpinner.isEnabled = true
        binding.toHourSpinner.isEnabled = true
        binding.toggleAmPmFrom.isEnabled = true
        binding.toggleAmPmTo.isEnabled = true
        binding.calendarView.isEnabled = true
    }

    private fun convertTo24Hour(hour: String, period: String): String {
        val h = hour.toInt()
        return when {
            period == "AM" && h == 12 -> "00:00"
            period == "AM" -> String.format("%02d:00", h)
            period == "PM" && h != 12 -> String.format("%02d:00", h + 12)
            else -> "12:00"
        }
    }

    private fun getTodayDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
