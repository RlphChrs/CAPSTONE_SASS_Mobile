package com.pilapil.sass.model

data class StudentProfile(
    val firstName: String,
    val lastName: String,
    val middleInitial: String? = null,
    val course: String? = null,
    val section: String? = null,
    val year: Int? = null,
    val email: String,
    val role: String? = null
)
