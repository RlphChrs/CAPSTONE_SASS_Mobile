package com.example.sass.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.sass.model.SubmissionFile
import com.example.sass.utils.FileUtils
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object SubmissionRepository {

    fun submit(context: Context, submissionFile: SubmissionFile, reason: String, onSuccess: () -> Unit) {
        val sessionManager = SessionManager(context)
        val token = sessionManager.getAuthToken()
        val studentId = sessionManager.getStudentId()

        if (token.isNullOrEmpty() || studentId.isNullOrEmpty()) {
            Toast.makeText(context, "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val file = FileUtils.getFileFromUri(context, submissionFile.uri)
                if (file == null) {
                    showToast(context, "Invalid file selected.")
                    return@launch
                }

                val fileRequestBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("File", file.name, fileRequestBody)

                // ✅ Add reason as plain text part
                val reasonPart: RequestBody = reason.toRequestBody("text/plain".toMediaTypeOrNull())

                val apiService = ApiService.create()

                val response = apiService.uploadStudentFile("Bearer $token", filePart, reasonPart)

                // ✅ Safely access backend response values
                val fileUrl = response.fileUrl ?: ""
                val fileName = response.fileName ?: file.name

                if (fileUrl.isBlank()) {
                    Log.e("SubmissionRepository", "❌ fileUrl is blank after upload.")
                    showToast(context, "Failed to upload file. Try again.")
                    return@launch
                }

                Log.d("SubmissionRepository", "📄 Uploaded fileUrl: $fileUrl")
                Log.d("SubmissionRepository", "📄 Uploaded fileName: $fileName")

                showToast(context, "Submission successful!")
                onSuccess()

            } catch (e: Exception) {
                Log.e("SubmissionRepository", "❌ Error submitting file", e)
                showToast(context, "Failed to submit file: ${e.message}")
            }
        }
    }

    private fun showToast(context: Context, message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
