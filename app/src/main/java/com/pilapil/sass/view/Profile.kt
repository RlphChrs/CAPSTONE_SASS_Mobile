package com.pilapil.sass.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.pilapil.sass.R
import com.pilapil.sass.api.RetrofitInstance
import com.pilapil.sass.repository.StudentProfileRepository
import com.pilapil.sass.utils.SessionManager
import com.pilapil.sass.viewModel.ProfileViewModel
import com.pilapil.sass.viewModel.ProfileViewModelFactory

class Profile : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var sessionManager: SessionManager

    private lateinit var tvUserId: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etCourse: EditText
    private lateinit var spinnerYear: Spinner
    private lateinit var etSection: EditText
    private lateinit var btnEdit: Button

    private val yearOptions = arrayOf("1", "2", "3", "4", "5")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sessionManager = SessionManager(requireContext())
        val repo = StudentProfileRepository(RetrofitInstance.apiService)
        viewModel = ViewModelProvider(requireActivity(), ProfileViewModelFactory(repo)).get(ProfileViewModel::class.java)

        // UI References
        tvUserId = view.findViewById(R.id.tv_user_id)
        tvUserName = view.findViewById(R.id.tv_user_name)
        tvUserEmail = view.findViewById(R.id.tv_user_email)
        etFirstName = view.findViewById(R.id.et_first_name)
        etLastName = view.findViewById(R.id.et_last_name)
        etCourse = view.findViewById(R.id.et_course)
        etSection = view.findViewById(R.id.et_section)
        spinnerYear = view.findViewById(R.id.spinner_year)
        btnEdit = view.findViewById(R.id.btn_edit)

        spinnerYear.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, yearOptions)

        val studentId = sessionManager.getStudentId()
        val token = sessionManager.getAuthToken()

        if (!studentId.isNullOrEmpty() && !token.isNullOrEmpty()) {
            viewModel.fetchProfile(studentId, "Bearer $token")
        } else {
            Toast.makeText(requireContext(), "Missing token or student ID", Toast.LENGTH_SHORT).show()
        }

        setupObservers()

        btnEdit.setOnClickListener {
            val dialog = EditProfileDialogFragment()
            dialog.show(parentFragmentManager, "EditProfileDialog")
        }

        return view
    }

    private fun setupObservers() {
        viewModel.studentProfile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                tvUserId.text = "ID: ${sessionManager.getStudentId()}"
                tvUserName.text = "${profile.firstName} ${profile.lastName}"
                tvUserEmail.text = profile.email

                // Fill and disable fields
                etFirstName.setText(profile.firstName)
                etFirstName.isEnabled = false


                etLastName.setText(profile.lastName)
                etLastName.isEnabled = false

                etCourse.setText(profile.course ?: "")
                etCourse.isEnabled = false

                etSection.setText(profile.section ?: "")
                etSection.isEnabled = false

                val yearIndex = yearOptions.indexOf(profile.year?.toString() ?: "1")
                spinnerYear.setSelection(if (yearIndex >= 0) yearIndex else 0)
                spinnerYear.isEnabled = false

                // ✅ Update Navigation Drawer Header
                val navigationView = requireActivity().findViewById<NavigationView>(R.id.navigation_view)
                val headerView = navigationView.getHeaderView(0)
                val headerName = headerView.findViewById<TextView>(R.id.user_name)
                val headerRole = headerView.findViewById<TextView>(R.id.user_role)

                headerName.text = "${profile.firstName} ${profile.lastName}"
                headerRole.text = profile.course ?: "Student"
            } else {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { updated ->
            if (updated) {
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()

                val studentId = sessionManager.getStudentId()
                val token = sessionManager.getAuthToken()
                if (!studentId.isNullOrEmpty() && !token.isNullOrEmpty()) {
                    viewModel.fetchProfile(studentId, "Bearer $token")
                }
            }
        }
    }
}
