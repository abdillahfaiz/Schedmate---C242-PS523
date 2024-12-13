package com.dicoding.schedmate.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.schedmate.data.pref.UserPreference

class LoginViewModelFactory(private val userPreference: UserPreference) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
