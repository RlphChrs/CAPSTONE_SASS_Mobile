package com.pilapil.sass.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("SASS_PREFS", Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    // ✅ Save Auth Token
    fun saveAuthToken(token: String) {
        editor.putString("AUTH_TOKEN", token)
        editor.apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString("AUTH_TOKEN", null)
    }

    // ✅ Save School ID
    fun saveSchoolId(schoolId: String) {
        editor.putString("SCHOOL_ID", schoolId)
        editor.apply()
    }

    fun getSchoolId(): String? {
        return sharedPreferences.getString("SCHOOL_ID", null)
    }

    // ✅ Save Student ID
    fun saveStudentId(studentId: String) {
        editor.putString("STUDENT_ID", studentId)
        editor.apply()
    }

    // ✅ Get Student ID
    fun getStudentId(): String? {
        return sharedPreferences.getString("STUDENT_ID", null)
    }

    // ✅ Clear Session
    fun clearSession() {
        editor.clear()
        editor.apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("accessToken", null)
    }

}
