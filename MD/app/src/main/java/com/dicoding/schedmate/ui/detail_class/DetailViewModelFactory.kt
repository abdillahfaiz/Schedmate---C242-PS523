package com.dicoding.schedmate.ui.detail_class

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.ui.home.HomeViewModel

class DetailViewModelFactory(private val userPreference: UserPreference) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailClassViewModel::class.java)) {
            return DetailClassViewModel(userPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}