package com.pilapil.sass.model

data class FileSubmissionRequest(
    val fileUrl: String,
    val fileName: String,
    val reason: String
)
