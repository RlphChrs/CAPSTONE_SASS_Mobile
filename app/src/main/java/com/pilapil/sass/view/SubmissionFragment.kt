package com.example.sass.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sass.adapter.SubmissionAdapter
import com.example.sass.viewmodel.SubmissionViewModel
import com.pilapil.sass.R

class SubmissionFragment : Fragment() {

    private lateinit var viewModel: SubmissionViewModel
    private lateinit var adapter: SubmissionAdapter
    private lateinit var recyclerFiles: RecyclerView
    private lateinit var editReason: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnBrowse: Button

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    requireActivity().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    viewModel.addFile(uri, requireContext())
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_submission, container, false)

        viewModel = ViewModelProvider(this)[SubmissionViewModel::class.java]
        recyclerFiles = view.findViewById(R.id.recyclerFiles)
        editReason = view.findViewById(R.id.editReason)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnBrowse = view.findViewById(R.id.btnBrowse)

        adapter = SubmissionAdapter(viewModel.selectedFiles) { file ->
            viewModel.removeFile(file)
        }

        recyclerFiles.layoutManager = LinearLayoutManager(requireContext())
        recyclerFiles.adapter = adapter

        viewModel.filesLiveData.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        btnBrowse.setOnClickListener {
            pickFile()
        }

        btnSubmit.setOnClickListener {
            val reason = editReason.text.toString().trim()
            if (reason.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a reason.", Toast.LENGTH_SHORT).show()
            } else if (viewModel.selectedFiles.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a file.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.submitFiles(requireContext(), reason) {
                    editReason.setText("")
                    viewModel.clearForm()
                }
            }
        }

        return view
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        filePickerLauncher.launch(intent)
    }
}
