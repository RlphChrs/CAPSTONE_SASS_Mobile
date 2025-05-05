package com.pilapil.sass.model

data class ReportRequest(
    val nameOfPerson: String,
    val idNumberOfPerson: String,
    val reason: String,
    val description: String
)
