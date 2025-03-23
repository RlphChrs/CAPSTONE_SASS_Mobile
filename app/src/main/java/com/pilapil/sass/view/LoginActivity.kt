package com.pilapil.sass.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pilapil.sass.MainActivity
import com.pilapil.sass.R
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.repository.StudentAuthRepository
import com.pilapil.sass.utils.SessionManager
import com.pilapil.sass.viewModel.StudentAuthViewModel
import com.pilapil.sass.viewModel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var authViewModel: StudentAuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        authViewModel = ViewModelProvider(
            this,
            ViewModelFactory(StudentAuthRepository(ApiService.create()))
        )[StudentAuthViewModel::class.java]

        val emailInput = findViewById<EditText>(R.id.et_username)
        val passwordInput = findViewById<EditText>(R.id.et_password)
        val loginButton = findViewById<Button>(R.id.btn_sign_in)
        val registerText = findViewById<TextView>(R.id.tv_create_account)

        registerText.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.loginStudent(email, password,
                onSuccess = onSuccess@{ token, studentId, schoolName ->
                    Log.d("LoginDebug", "Token: $token")
                    Log.d("LoginDebug", "Student ID: $studentId")
                    Log.d("LoginDebug", "School Name: $schoolName")

                    if (token.isEmpty() || studentId.isEmpty() || schoolName.isEmpty()) {
                        Toast.makeText(
                            this,
                            "Login failed: Missing required data",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@onSuccess
                    }

                    sessionManager.saveAuthToken(token)
                    sessionManager.saveStudentId(studentId)
                    sessionManager.saveSchoolId(schoolName)

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                },
                onError = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
