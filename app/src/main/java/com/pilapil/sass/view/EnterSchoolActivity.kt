package com.pilapil.sass.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.pilapil.sass.MainActivity
import com.pilapil.sass.R
import com.pilapil.sass.viewmodel.EnterSchoolViewModel

class EnterSchoolActivity : AppCompatActivity() {

    private lateinit var viewModel: EnterSchoolViewModel
    private lateinit var etLink: EditText
    private lateinit var btnEnter: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_enter_school)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔹 Manually Initialize ViewModel
        viewModel = ViewModelProvider(this)[EnterSchoolViewModel::class.java]

        etLink = findViewById(R.id.et_link)
        btnEnter = findViewById(R.id.btn_enter)

        // 🔹 Check if school name is already saved
        val savedSchool = viewModel.getSavedSchoolName()
        if (!savedSchool.isNullOrEmpty()) {
            navigateToChatbot()
        }

        btnEnter.setOnClickListener {
            val schoolName = etLink.text.toString().trim()
            if (schoolName.isNotEmpty()) {
                viewModel.saveSchoolName(schoolName)
                navigateToChatbot()
            } else {
                Toast.makeText(this, "Please enter a school name!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToChatbot() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
