package com.pilapil.sass

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.pilapil.sass.utils.NavigationHandler
import com.pilapil.sass.utils.SessionManager
import com.pilapil.sass.view.ChatFragment
import com.pilapil.sass.view.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuIcon: ImageView
    private lateinit var navigationView: NavigationView
    private lateinit var navigationHandler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val sessionManager = SessionManager(this)
        val studentId = sessionManager.getAuthToken()
        if (studentId == null) {
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        //  UI Components
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        menuIcon = findViewById(R.id.menu_icon)

        navigationHandler = NavigationHandler(this, drawerLayout, navigationView)
        navigationHandler.setupNavigation()

        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, ChatFragment())
            .commit()
    }

    private fun logoutUser() {
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
