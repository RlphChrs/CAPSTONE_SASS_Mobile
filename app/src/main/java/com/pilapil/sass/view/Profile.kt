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
    private lateinit var tvFirstName: TextView
    private lateinit var tvLastName: TextView
    private lateinit var tvCourse: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvSection: TextView
    private lateinit var btnEdit: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sessionManager = SessionManager(requireContext())
        val repo = StudentProfileRepository(RetrofitInstance.apiService)
        viewModel = ViewModelProvider(
            requireActivity(),
            ProfileViewModelFactory(repo)
        ).get(ProfileViewModel::class.java)

        // UI References
        tvUserId = view.findViewById(R.id.tv_user_id)
        tvUserName = view.findViewById(R.id.tv_user_name)
        tvUserEmail = view.findViewById(R.id.tv_user_email)
        tvFirstName = view.findViewById(R.id.tv_first_name)
        tvLastName = view.findViewById(R.id.tv_last_name)
        tvCourse = view.findViewById(R.id.tv_course)
        tvYear = view.findViewById(R.id.tv_year)
        tvSection = view.findViewById(R.id.tv_section)
        btnEdit = view.findViewById(R.id.btn_edit)

        val studentId = sessionManager.getStudentId()
        val token = sessionManager.getAuthToken()

        if (!token.isNullOrEmpty()) {
            viewModel.fetchProfile("Bearer $token")

        } else {
            Toast.makeText(requireContext(), "Missing token or student ID", Toast.LENGTH_SHORT).show()
        }

        setupObservers()

        btnEdit.setOnClickListener {
            // You can later implement a dialog or image picker for profile photo update
            Toast.makeText(requireContext(), "Feature: Change profile picture", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun setupObservers() {
        viewModel.studentProfile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                tvUserId.text = "ID: ${sessionManager.getStudentId()}"
                tvUserName.text = "${profile.firstName} ${profile.lastName}"
                tvUserEmail.text = profile.email

                tvFirstName.text = profile.firstName
                tvLastName.text = profile.lastName
                tvCourse.text = profile.course ?: "-"
                tvSection.text = profile.section ?: "-"
                tvYear.text = profile.year?.toString() ?: "-"

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
                if (!token.isNullOrEmpty()) {
                    viewModel.fetchProfile("Bearer $token")
                }

            }
        }
    }
}
