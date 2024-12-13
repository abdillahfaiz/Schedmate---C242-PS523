package com.dicoding.schedmate.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.dicoding.schedmate.R

class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ROLE = "user_role"
        const val IS_LOGIN = "is_login"
    }

    fun saveAuthToken(token : String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun getAuthToken() : String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveRole(role : String) {
        val editor = prefs.edit()
        editor.putString(USER_ROLE, role)
        editor.apply()
    }

    fun getRole () : String? {
        return prefs.getString(USER_ROLE, null)
    }

    fun updateRole(role : String) {
        val editor = prefs.edit()
        editor.putString(USER_ROLE, role)
        editor.apply()
    }

    fun updateIsLogin(isLogin : Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(IS_LOGIN, isLogin)
        editor.apply()
    }

    fun getIsLogin() : Boolean {
        return prefs.getBoolean(IS_LOGIN, false)
    }

    fun doLogout(){
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, "")
        editor.putString(USER_ROLE, "")
        editor.putBoolean(IS_LOGIN, false)
    }
 }