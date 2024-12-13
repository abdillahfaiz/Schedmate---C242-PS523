package com.dicoding.schedmate.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val  Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>){

    suspend fun saveSession(user: UserSession) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = user.token
            preferences[ROLE_KEY] = user.role
            preferences[USER_ID_KEY] = user.userId
            preferences[IS_LOGIN_KEY] = true
        }
    }

    fun getSession() : Flow<UserSession> {
        return dataStore.data.map { preferences ->
            UserSession(
                token = preferences[TOKEN_KEY] ?: "",
                role = preferences[ROLE_KEY] ?: "",
                userId = preferences[USER_ID_KEY] ?: 0,
                isLogin = preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = ""
            preferences[ROLE_KEY] = ""
            preferences[USER_ID_KEY] = 0
            preferences[IS_LOGIN_KEY] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val TOKEN_KEY = stringPreferencesKey("token")
        private val ROLE_KEY = stringPreferencesKey("role")
        private val USER_ID_KEY = intPreferencesKey("userId")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}