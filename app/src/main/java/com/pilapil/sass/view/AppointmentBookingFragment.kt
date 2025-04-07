package com.pilapil.sass.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pilapil.sass.R
import com.pilapil.sass.databinding.FragmentAppointmentBookingBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import java.util.Date

class AppointmentBookingFragment : Fragment() {

    private var _binding: FragmentAppointmentBookingBinding? = null
    private val binding get() = _binding!!
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selectedDate = arguments?.getString("SELECTED_DATE") ?: getTodayDate()

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = formatter.parse(selectedDate)!!
        binding.calendarView.date = calendar.timeInMillis

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = formatter.format(calendar.time)
        }

        setupTimePickers()

        binding.btnConfirmSelection.setOnClickListener {
            val description = binding.etDescription.text.toString().trim()
            val fromHour = binding.fromHourSpinner.selectedItem.toString()
            val fromPeriod = if (binding.buttonAMFrom.isChecked) "AM" else "PM"
            val toHour = binding.toHourSpinner.selectedItem.toString()
            val toPeriod = if (binding.buttonAMTo.isChecked) "AM" else "PM"

            val fromTime = "$fromHour:00 $fromPeriod"
            val toTime = "$toHour:00 $toPeriod"
            val fullDateTime = "$selectedDate, $fromTime - $toTime"

            showConfirmationDialog(description, fullDateTime)
        }
    }

    private fun setupTimePickers() {
        val hours = (1..12).map { String.format("%02d", it) }
        val hourAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, hours)

        binding.fromHourSpinner.adapter = hourAdapter
        binding.toHourSpinner.adapter = hourAdapter

        // Update TextViews beside spinners
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

        // AM/PM toggles
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

    private fun showConfirmationDialog(description: String, dateTime: String) {
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
            // TODO: Submit booking to backend
        }

        dialog.show()
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
