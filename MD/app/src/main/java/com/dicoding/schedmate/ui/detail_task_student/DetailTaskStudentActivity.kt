package com.dicoding.schedmate.ui.detail_task_student

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.schedmate.R
import com.dicoding.schedmate.databinding.ActivityDetailTaskStudentBinding

class DetailTaskStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTaskStudentBinding

    companion object {
        const val ID_TASK = "id_task"
        const val ID_CLASS = "id_class"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTaskStudentBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

    }
}