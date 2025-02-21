package com.pilapil.sass.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("SASS_PREFS", Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveAuthToken(token: String) {
        editor.putString("AUTH_TOKEN", token)
        editor.apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString("AUTH_TOKEN", null)
    }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}
