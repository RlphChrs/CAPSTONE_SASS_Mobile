package com.pilapil.sass.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.pilapil.sass.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AppointmentBooking : AppCompatActivity() {

    private lateinit var dateEditText: EditText
    private lateinit var fromHour: EditText
    private lateinit var fromMinute: EditText
    private lateinit var toHour: EditText
    private lateinit var toMinute: EditText
    private lateinit var fromAmPmGroup: MaterialButtonToggleGroup
    private lateinit var toAmPmGroup: MaterialButtonToggleGroup
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var selectedDateTime: TextInputEditText
    private lateinit var addButton: Button

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_appointment_booking)

        // Initialize Views
        dateEditText = findViewById(R.id.et_date)
        fromHour = findViewById(R.id.fromHour)
        fromMinute = findViewById(R.id.fromMinute)
        toHour = findViewById(R.id.toHour)
        toMinute = findViewById(R.id.toMinute)
        fromAmPmGroup = findViewById(R.id.toggleAmPm_from)
        toAmPmGroup = findViewById(R.id.buttonAM_to) // Corrected ID
        descriptionEditText = findViewById(R.id.et_description)
        selectedDateTime = findViewById(R.id.et_date_time)
        addButton = findViewById(R.id.btn_add)

        // Set Listeners
        dateEditText.setOnClickListener { showDatePickerDialog() }
        addButton.setOnClickListener { saveAppointment() }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                dateEditText.setText(dateFormat.format(calendar.time))
                updateDateTimeDisplay()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun updateDateTimeDisplay() {
        val fromTime = getTimeFormatted(fromHour.text.toString(), fromMinute.text.toString(), getSelectedAmPm(fromAmPmGroup))
        val toTime = getTimeFormatted(toHour.text.toString(), toMinute.text.toString(), getSelectedAmPm(toAmPmGroup))

        val formattedDateTime = String.format(Locale.getDefault(), "%s, %s - %s", dateFormat.format(calendar.time), fromTime, toTime)
        selectedDateTime.text = Editable.Factory.getInstance().newEditable(formattedDateTime)
    }

    private fun getTimeFormatted(hour: String, minute: String, amPm: String): String {
        if (hour.isEmpty() || minute.isEmpty()) return "--:-- --"

        val hourInt = hour.toIntOrNull() ?: return "--:-- --"
        val minuteInt = minute.toIntOrNull() ?: return "--:-- --"

        return String.format(Locale.getDefault(), "%02d:%02d %s", hourInt, minuteInt, amPm)
    }

    private fun getSelectedAmPm(toggleGroup: MaterialButtonToggleGroup): String {
        return when (toggleGroup.checkedButtonId) {
            R.id.buttonAM_from, R.id.buttonAM_to -> "AM"
            R.id.buttonPM_from, R.id.buttonPM_to -> "PM"
            else -> "--"
        }
    }

    private fun saveAppointment() {
        if (dateEditText.text.isEmpty() || fromHour.text.isEmpty() || fromMinute.text.isEmpty() || toHour.text.isEmpty() || toMinute.text.isEmpty()) {
            selectedDateTime.text = Editable.Factory.getInstance().newEditable("Incomplete details")
            return
        }
        updateDateTimeDisplay()
    }
}
