package com.pilapil.sass.repository

import android.content.Context
import android.content.SharedPreferences

class SchoolRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    fun saveSchoolName(schoolName: String) {
        sharedPreferences.edit().putString("SCHOOL_NAME", schoolName).apply()
    }

    fun getSchoolName(): String? {
        return sharedPreferences.getString("SCHOOL_NAME", null)
    }
}
