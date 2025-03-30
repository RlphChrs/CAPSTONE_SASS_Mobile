package com.pilapil.sass.utils

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.sass.view.SubmissionFragment
import com.google.android.material.navigation.NavigationView
import com.pilapil.sass.R
import com.pilapil.sass.view.*

class NavigationHandler(
    private val context: AppCompatActivity,
    private val drawerLayout: DrawerLayout,
    private val navigationView: NavigationView
) {
    fun setupNavigation() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_new_chat -> {
                    context.supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, ChatFragment())
                        .addToBackStack(null)
                        .commit()
                }
                R.id.nav_calendar -> {
                    context.supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, Calendar())
                        .addToBackStack(null)
                        .commit()
                }
                R.id.nav_notifications -> {
                    context.supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, Notification())
                        .addToBackStack(null)
                        .commit()
                }
                R.id.nav_reports -> {
                    context.supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, Report())
                        .addToBackStack(null)
                        .commit()
                }
                R.id.nav_history -> {
                    context.supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, HistoryFragment())
                        .addToBackStack(null)
                        .commit()
                }
                R.id.nav_submission -> {
                    context.supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, SubmissionFragment())
                        .addToBackStack(null)
                        .commit()
                }
                R.id.nav_logout -> {
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    context.finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}
