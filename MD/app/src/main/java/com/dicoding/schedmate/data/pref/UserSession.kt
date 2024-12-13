package com.dicoding.schedmate.data.pref

data class UserSession(
    val token: String,
    val role: String,
    val userId: Int,
    val isLogin: Boolean = false
)
