package com.pilapil.sass.model

data class FileUploadResponse(
    val fileUrl: String,
    val fileName: String,
    val submissionId: String?,
    val message: String? = null
)
