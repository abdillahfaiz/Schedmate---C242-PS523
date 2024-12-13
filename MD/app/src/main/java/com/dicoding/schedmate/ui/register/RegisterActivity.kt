package com.dicoding.schedmate.ui.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.schedmate.MainActivity
import com.dicoding.schedmate.R
import com.dicoding.schedmate.databinding.ActivityRegisterBinding
import com.dicoding.schedmate.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    companion object {
         const val ROLE_SELECTED = "ROLE"
    }

    private lateinit var binding : ActivityRegisterBinding
    private var isPasswordVisible = false
    private lateinit var viewModel: RegisterViewModel

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val role = intent.getStringExtra(ROLE_SELECTED)?.uppercase()

        binding.tvTitleDaftar.text = "Daftar. - $role"

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        //---OBSERVE VIEW MODEL SECTION----
        viewModel.registerResponse.observe(this) {registerResponse ->
            if (registerResponse != null){
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading)  binding.btnRegist.text = "Loading..." else binding.btnRegist.text = "Daftar"
        }
        viewModel.snackBarText.observe(this) {message ->
            Toast.makeText(this, message + " Silahkan login terlebih dahulu", Toast.LENGTH_SHORT).show()
        }

        binding.tvMasuk.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            intent.putExtra("FROM_LOGIN", true)
            startActivity(intent)
        }

        binding.btnRegist.setOnClickListener{onRegister()}

        binding.etPassword.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Cek apakah drawable kanan disentuh
                if (event.rawX >= (binding.etPassword.right - binding.etPassword.compoundDrawables[2].bounds.width())) {
                    togglePasswordVisibility(binding.etPassword)
                    return@setOnTouchListener true
                }
            }
            false
        }

    }

    private fun onRegister() {
        val usernameResult = binding.etName.text?.toString()?.trim()
        val emailResult = binding.etEmail.text?.toString()?.trim()
        val passResult = binding.etPassword.text?.toString()?.trim()
        val role = intent.getStringExtra(ROLE_SELECTED)

        var isValid = true

        if (usernameResult.isNullOrEmpty()) {
            binding.etName.error = "Nama tidak boleh kosong"
            isValid = false
        }

        if (emailResult.isNullOrEmpty()) {
            binding.etEmail.error = "Email tidak boleh kosong"
            isValid = false
        }

        if (passResult.isNullOrEmpty()) {
            binding.etPassword.error = "Password tidak boleh kosong"
            isValid = false
        }

        if (isValid && role != null) {
            viewModel.doRegister(usernameResult.toString(), emailResult.toString(), passResult.toString(), role )
        }
    }

    private fun togglePasswordVisibility(editText: EditText) {
        if (isPasswordVisible) {
            // Ubah ke mode password tersembunyi
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visible, 0)
        } else {
            // Ubah ke mode password terlihat
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visible_off, 0)
        }
        isPasswordVisible = !isPasswordVisible
        // Pindahkan kursor ke akhir teks
        editText.setSelection(editText.text.length)
    }
}