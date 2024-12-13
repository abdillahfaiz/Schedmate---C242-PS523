package com.dicoding.schedmate.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.schedmate.R
import com.dicoding.schedmate.databinding.ActivityMainStudentBinding

class MainStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main_student)

        setupActionBarWithNavController(navController)
        navView.setupWithNavController(navController)
    }
}