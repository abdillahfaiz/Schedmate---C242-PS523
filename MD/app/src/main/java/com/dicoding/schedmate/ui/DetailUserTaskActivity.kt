package com.dicoding.schedmate.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.schedmate.R
import com.dicoding.schedmate.databinding.ActivityDetailUserTaskBinding

class DetailUserTaskActivity : AppCompatActivity() {

    companion object {
        const val IMAGE = ""
        const val EMAIL = "extra_email"
        const val USERNAME = "extra_username"
        const val PHOTO_PROFILE = ""
    }

    private lateinit var binding: ActivityDetailUserTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailUserTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val image = intent.getStringExtra(IMAGE)
        val email = intent.getStringExtra(EMAIL)
        val username = intent.getStringExtra(USERNAME)
        val photoProfile = intent.getStringExtra(PHOTO_PROFILE)

        Log.d("DetailUserTaskActivity", "onCreate: $image, $email, $username, $photoProfile")

        binding.ivBackButton.setOnClickListener {
            finish()
        }

        if (photoProfile != "") {
            Glide.with(this)
                .load(photoProfile)
                .into(binding.circleImageView)
        }

        if (image != "") {
            Glide.with(this)
                .load(image)
                .into(binding.ivImage)
        }

        binding.tvUsername.text = username
        binding.tvEmail.text = email

    }
}