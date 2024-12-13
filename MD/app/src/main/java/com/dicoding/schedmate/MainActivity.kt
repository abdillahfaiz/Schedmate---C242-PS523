package com.dicoding.schedmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.schedmate.data.pref.SessionManager
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.dataStore
import com.dicoding.schedmate.databinding.ActivityMainBinding
import com.dicoding.schedmate.ui.MainStudentActivity
import com.dicoding.schedmate.ui.MainTeacherActivity
import com.dicoding.schedmate.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        sessionManager = SessionManager(this)
        Log.d("IS LOGIN", sessionManager.getIsLogin().toString())
        Log.d("ROLE", sessionManager.getRole().toString())
        Log.d("TOKEN", sessionManager.getAuthToken().toString())

        if (!sessionManager.getIsLogin()) {
            // Arahkan ke Login jika belum login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
//            startActivity(Intent(this, MainTeacherActivity::class.java))
            if (sessionManager.getRole() == "murid") {
                startActivity(Intent(this, MainStudentActivity::class.java))
            } else {
                startActivity(Intent(this, MainTeacherActivity::class.java))
            }
            finish() // Tutup MainActivity ini
        }
    }
}
