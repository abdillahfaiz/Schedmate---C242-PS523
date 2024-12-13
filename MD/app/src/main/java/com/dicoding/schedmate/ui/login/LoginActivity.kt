package com.dicoding.schedmate.ui.login

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
import com.dicoding.schedmate.SelectRoleActivity
import com.dicoding.schedmate.data.pref.SessionManager
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.dataStore
import com.dicoding.schedmate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(){

    private lateinit var binding : ActivityLoginBinding
    private var isPasswordVisible = false
    private lateinit var viewModel: LoginViewModel
    private lateinit var sessionManager: SessionManager

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        val userPreference = UserPreference.getInstance(dataStore)
        val factory = LoginViewModelFactory(userPreference)
        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        viewModel.loginRes.observe(this) {loginRes ->
            if (loginRes != null){
                sessionManager.updateIsLogin(isLogin = true)
                sessionManager.saveRole(loginRes.role)
                sessionManager.saveAuthToken(loginRes.token)
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("ROUTE", "LOGIN")
                startActivity(intent)
            }
        }

        viewModel.isLoding.observe(this) {isLoading ->
            if (isLoading)  binding.btnLogin.text = "Loading..." else binding.btnLogin.text = "Masuk"
        }

        viewModel.snackBarText.observe(this) {message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        binding.tvDaftar.setOnClickListener {
            val intent = Intent(this@LoginActivity, SelectRoleActivity::class.java )
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener{onLogin()}

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

    private fun onLogin() {
        val emailResult = binding.etEmail.text?.toString()?.trim()
        val passResult = binding.etPassword.text?.toString()?.trim()

        var isValid = true

        if (emailResult.isNullOrEmpty()) {
            binding.etEmail.error = "Email tidak boleh kosong"
            isValid = false
        }

        if (passResult.isNullOrEmpty()) {
            binding.etPassword.error = "Password tidak boleh kosong"
            isValid = false
        }

        if (isValid) {
            viewModel.doLogin(emailResult.toString(), passResult.toString())
        }
    }


    private fun togglePasswordVisibility(editText: EditText) {
        if (isPasswordVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visible, 0)
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visible_off, 0)
        }
        isPasswordVisible = !isPasswordVisible
        editText.setSelection(editText.text.length)
    }


}