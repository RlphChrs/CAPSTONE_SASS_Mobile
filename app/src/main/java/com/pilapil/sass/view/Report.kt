package com.pilapil.sass.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.pilapil.sass.R
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.model.ReportRequest
import com.pilapil.sass.repository.ReportRepository
import com.pilapil.sass.utils.SessionManager
import com.pilapil.sass.viewmodel.ReportViewModel

class Report : Fragment() {

    private lateinit var reportViewModel: ReportViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val checkboxUnderstand = view.findViewById<CheckBox>(R.id.checkboxUnderstand)
        val inputName = view.findViewById<EditText>(R.id.inputName)
        val inputIdNumber = view.findViewById<EditText>(R.id.inputIdNumber)
        val inputReason = view.findViewById<EditText>(R.id.inputReason)
        val inputDescription = view.findViewById<EditText>(R.id.inputDescription)
        val checkboxConfirm = view.findViewById<CheckBox>(R.id.checkboxConfirm)
        val btnDone = view.findViewById<Button>(R.id.btnDone)

        sessionManager = SessionManager(requireContext())

        // Initialize API and Repository
        val apiService = ApiService.create()
        val repository = ReportRepository(apiService)

        // Initialize ViewModel manually
        reportViewModel = ReportViewModel(repository)

        btnDone.setOnClickListener {
            if (!checkboxUnderstand.isChecked || !checkboxConfirm.isChecked) {
                Toast.makeText(requireContext(), "Please check both checkboxes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = inputName.text.toString().trim()
            val idNumber = inputIdNumber.text.toString().trim()
            val reason = inputReason.text.toString().trim()
            val description = inputDescription.text.toString().trim()

            if (name.isEmpty() || idNumber.isEmpty() || reason.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = sessionManager.getAuthToken()
            if (token.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "No token found. Please log in again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val report = ReportRequest(name, idNumber, reason, description)

            reportViewModel.submitReport(token, report) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Report submitted successfully!", Toast.LENGTH_SHORT).show()
                    inputName.text.clear()
                    inputIdNumber.text.clear()
                    inputReason.text.clear()
                    inputDescription.text.clear()
                    checkboxUnderstand.isChecked = false
                    checkboxConfirm.isChecked = false
                } else {
                    Toast.makeText(requireContext(), "Failed to submit report", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
