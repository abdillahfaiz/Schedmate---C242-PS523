package com.dicoding.schedmate.ui.create_task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.ui.detail_class.DetailClassViewModel

class CreateTaskViewModelFactory(private val userPreference: UserPreference) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateTaskViewModel::class.java)) {
            return CreateTaskViewModel(userPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}