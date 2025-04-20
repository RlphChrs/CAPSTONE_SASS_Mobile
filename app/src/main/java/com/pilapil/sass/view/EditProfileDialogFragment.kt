package com.pilapil.sass.view

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.pilapil.sass.R
import com.pilapil.sass.utils.SessionManager
import com.pilapil.sass.viewModel.ProfileViewModel

class EditProfileDialogFragment : DialogFragment() {

    private lateinit var etCourse: EditText
    private lateinit var etSection: EditText
    private lateinit var spinnerYear: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private lateinit var viewModel: ProfileViewModel
    private lateinit var sessionManager: SessionManager

    private val yearOptions = arrayOf("1", "2", "3", "4")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_edit_profile_dialog)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        setupUI()
    }

    private fun setupUI() {
        val view = dialog?.findViewById<View>(android.R.id.content)
        if (view != null) {
            etCourse = view.findViewById(R.id.et_course)
            etSection = view.findViewById(R.id.et_section)
            spinnerYear = view.findViewById(R.id.spinner_year)
            btnSave = view.findViewById(R.id.btn_save)
            btnCancel = view.findViewById(R.id.btn_cancel)

            sessionManager = SessionManager(requireContext())
            viewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]

            spinnerYear.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, yearOptions)

            // Pre-fill fields from profile
            viewModel.studentProfile.value?.let { profile ->
                etCourse.setText(profile.course ?: "")
                etSection.setText(profile.section ?: "")
                val yearIndex = yearOptions.indexOf(profile.year?.toString() ?: "1")
                spinnerYear.setSelection(if (yearIndex >= 0) yearIndex else 0)
            }

            btnSave.setOnClickListener {
                val course = etCourse.text.toString().trim()
                val section = etSection.text.toString().trim()
                val year = spinnerYear.selectedItem.toString().toIntOrNull()

                if (course.isEmpty() || section.isEmpty() || year == null) {
                    Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val updates = mutableMapOf<String, Any?>(
                    "course" to course,
                    "section" to section,
                    "year" to year
                )

                val token = sessionManager.getAuthToken()
                if (!token.isNullOrEmpty()) {
                    Log.d("EDIT_PROFILE", "Clicked SAVE")
                    Log.d("EDIT_PROFILE", "Token: Bearer $token")
                    Log.d("EDIT_PROFILE", "Update Payload: $updates")

                    viewModel.updateProfile("Bearer $token", updates)
                } else {
                    Log.e("EDIT_PROFILE", "❌ No token found!")
                    Toast.makeText(requireContext(), "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show()
                }

                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }
}
