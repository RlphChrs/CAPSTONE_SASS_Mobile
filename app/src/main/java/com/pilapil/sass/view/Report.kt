package com.pilapil.sass.view


import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pilapil.sass.R

class Report : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_report) // Ensure this matches your XML filename

        // Initialize views
        val checkboxUnderstand = findViewById<CheckBox>(R.id.checkboxUnderstand)
        val inputName = findViewById<EditText>(R.id.inputName)
        val inputIdNumber = findViewById<EditText>(R.id.inputIdNumber)
        val inputReason = findViewById<EditText>(R.id.inputReason)
        val inputDescription = findViewById<EditText>(R.id.inputDescription)
        val checkboxConfirm = findViewById<CheckBox>(R.id.checkboxConfirm)
        val btnDone = findViewById<Button>(R.id.btnDone)

        btnDone.setOnClickListener {
            if (!checkboxUnderstand.isChecked || !checkboxConfirm.isChecked) {
                Toast.makeText(this, "Please check both checkboxes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = inputName.text.toString().trim()
            val idNumber = inputIdNumber.text.toString().trim()
            val reason = inputReason.text.toString().trim()
            val description = inputDescription.text.toString().trim()

            if (name.isEmpty() || idNumber.isEmpty() || reason.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Handle form submission logic (e.g., save to database, send to API)
            Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show()
        }
    }
}




