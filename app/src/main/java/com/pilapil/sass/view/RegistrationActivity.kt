package com.pilapil.sass.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pilapil.sass.R
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.model.Student
import com.pilapil.sass.repository.StudentAuthRepository
import com.pilapil.sass.viewModel.StudentAuthViewModel
import com.pilapil.sass.viewModel.ViewModelFactory

class RegistrationActivity : AppCompatActivity() {

    private lateinit var authViewModel: StudentAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize ViewModel manually
        authViewModel = ViewModelProvider(
            this,
            ViewModelFactory(StudentAuthRepository(ApiService.create()))
        )[StudentAuthViewModel::class.java]

        val firstNameInput = findViewById<EditText>(R.id.et_first_name)
        val lastNameInput = findViewById<EditText>(R.id.et_last_name)
        val emailInput = findViewById<EditText>(R.id.et_email)
        val passwordInput = findViewById<EditText>(R.id.et_password)
        val repeatPasswordInput = findViewById<EditText>(R.id.et_repeat_password)
        val termsCheckbox = findViewById<CheckBox>(R.id.cb_terms_conditions)
        val registerButton = findViewById<Button>(R.id.btn_register)
        val loginText = findViewById<TextView>(R.id.tv_already_have_account)

        registerButton.setOnClickListener {
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val repeatPassword = repeatPasswordInput.text.toString().trim()
            val termsAccepted = termsCheckbox.isChecked

            // 🔍 Debugging logs
            Log.d("Registration", "Password: '$password'")
            Log.d("Registration", "Repeat Password: '$repeatPassword'")
            Log.d("Registration", "Terms Checkbox Checked: $termsAccepted")

            // ✅ Input validation
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != repeatPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!termsAccepted) {
                Toast.makeText(this, "You must accept the terms and conditions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Ensure `repeatPassword` is included in the request
            val student = Student(
                userId = "stu${System.currentTimeMillis()}",
                email = email,
                password = password,
                repeatPassword = repeatPassword, // ✅ Now included
                firstName = firstName,
                lastName = lastName,
                role = "Student",
                createdAt = System.currentTimeMillis().toString(),
                termsAccepted = termsAccepted
            )

// ✅ Call ViewModel function with corrected parameters
            authViewModel.registerStudent(student,
                onSuccess = { message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },
                onError = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            )

        }

        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
