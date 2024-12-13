package com.dicoding.schedmate.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.ui.login.LoginViewModel

class HomeViewModelFactory(private val userPreference: UserPreference) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(userPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}