package com.dicoding.schedmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.schedmate.databinding.ActivitySelectRoleBinding
import com.dicoding.schedmate.ui.register.RegisterActivity

class SelectRoleActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySelectRoleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.llStudent.setOnClickListener {
            val intent = Intent(this@SelectRoleActivity, RegisterActivity::class.java)
            intent.putExtra(RegisterActivity.ROLE_SELECTED, "murid")
            startActivity(intent)
        }

        binding.llTeacher.setOnClickListener {
            val intent = Intent(this@SelectRoleActivity, RegisterActivity::class.java)
            intent.putExtra(RegisterActivity.ROLE_SELECTED, "guru")
            startActivity(intent)
        }
    }
}