package com.dicoding.schedmate.ui.detail_task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.ui.detail_class.DetailClassViewModel

class DetailTaskViewModelFactory(private val userPreference: UserPreference) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailTaskViewModel::class.java)) {
            return DetailTaskViewModel(userPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}