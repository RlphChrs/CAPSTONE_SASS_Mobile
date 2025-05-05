package com.example.sass.viewmodel

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sass.model.SubmissionFile
import com.example.sass.repository.SubmissionRepository

class SubmissionViewModel : ViewModel() {

    val selectedFiles = mutableListOf<SubmissionFile>()
    private val _filesLiveData = MutableLiveData<List<SubmissionFile>>()
    val filesLiveData: LiveData<List<SubmissionFile>> = _filesLiveData

    fun addFile(uri: Uri, context: Context) {
        val name = getFileNameFromUri(context, uri) ?: "Unnamed.pdf"
        val file = SubmissionFile(uri, name)
        selectedFiles.clear()
        selectedFiles.add(file)
        _filesLiveData.value = selectedFiles
    }

    fun removeFile(file: SubmissionFile) {
        selectedFiles.remove(file)
        _filesLiveData.value = selectedFiles
    }

    fun submitFiles(context: Context, reason: String, onSuccess: () -> Unit) {
        if (selectedFiles.isNotEmpty()) {
            SubmissionRepository.submit(context, selectedFiles[0], reason, onSuccess)
        } else {
            Toast.makeText(context, "No file selected.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    return it.getString(nameIndex)
                }
            }
        }
        return null
    }
    fun clearForm() {
        selectedFiles.clear()
        _filesLiveData.postValue(selectedFiles)
    }


}
