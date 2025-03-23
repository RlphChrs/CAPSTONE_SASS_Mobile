package com.pilapil.sass.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.pilapil.sass.R

class Report : Fragment() {

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

            Toast.makeText(requireContext(), "Report submitted successfully!", Toast.LENGTH_SHORT).show()
        }
    }
}




