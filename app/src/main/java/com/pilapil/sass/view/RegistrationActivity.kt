package com.pilapil.sass.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.pilapil.sass.R
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.model.Student
import com.pilapil.sass.repository.StudentAuthRepository
import com.pilapil.sass.viewModel.StudentAuthViewModel
import com.pilapil.sass.viewModel.ViewModelFactory

class RegistrationActivity : AppCompatActivity() {

    private lateinit var authViewModel: StudentAuthViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        authViewModel = ViewModelProvider(
            this,
            ViewModelFactory(StudentAuthRepository(ApiService.create()))
        )[StudentAuthViewModel::class.java]

        val studentIdInput = findViewById<EditText>(R.id.et_student_id)
        val emailInput = findViewById<EditText>(R.id.et_email)
        val schoolNameInput = findViewById<EditText>(R.id.et_school_name)
        val passwordInput = findViewById<EditText>(R.id.et_password)
        val repeatPasswordInput = findViewById<EditText>(R.id.et_repeat_password)
        val termsCheckbox = findViewById<CheckBox>(R.id.cb_terms_conditions)
        registerButton = findViewById(R.id.btn_register)
        val loginText = findViewById<TextView>(R.id.tv_already_have_account)
        progressBar = findViewById(R.id.registrationProgressBar)

        registerButton.setOnClickListener {
            val studentId = studentIdInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val schoolName = schoolNameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val repeatPassword = repeatPasswordInput.text.toString().trim()
            val termsAccepted = termsCheckbox.isChecked

            if (studentId.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || schoolName.isEmpty()) {
                showTopSnackbar("All fields are required.")
                return@setOnClickListener
            }

            if (password != repeatPassword) {
                showTopSnackbar("Passwords do not match.")
                return@setOnClickListener
            }

            if (!termsAccepted) {
                showTopSnackbar("You must accept the terms and conditions.")
                return@setOnClickListener
            }

            val student = Student(
                studentId = studentId,
                email = email,
                schoolName = schoolName,
                password = password,
                repeatPassword = repeatPassword,
                firstName = "",
                lastName = "",
                role = "Student",
                createdAt = System.currentTimeMillis().toString(),
                termsAccepted = termsAccepted
            )

            toggleLoading(true)

            authViewModel.registerStudent(student,
                onSuccess = { message, _ ->
                    toggleLoading(false)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },
                onError = { error ->
                    toggleLoading(false)
                    when {
                        error.contains("not found", ignoreCase = true) -> {
                            showTopSnackbar("Student ID not found.\nPlease check with your SAO.")
                        }
                        error.contains("email already", ignoreCase = true) -> {
                            showTopSnackbar("Email already registered. Try logging in.")
                        }
                        error.contains("school", ignoreCase = true) && error.contains("not registered", ignoreCase = true) -> {
                            showTopSnackbar("School not registered. Contact your SAO.")
                        }
                        else -> {
                            showTopSnackbar("$error")
                        }
                    }
                }
            )
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        registerButton.isEnabled = !isLoading
    }

    private fun showTopSnackbar(message: String) {
        val rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)

        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        snackbarView.layoutParams = params

        snackbarView.setBackgroundColor(Color.parseColor("#D32F2F"))
        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        textView.textSize = 14f
        textView.maxLines = 5

        snackbar.show()
    }
}
