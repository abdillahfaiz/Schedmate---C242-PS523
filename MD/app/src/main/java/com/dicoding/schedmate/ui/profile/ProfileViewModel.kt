package com.dicoding.schedmate.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.UserSession
import com.dicoding.schedmate.data.response.ProfileResponse
import com.dicoding.schedmate.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel (private val userPreference: UserPreference) : ViewModel() {
    private val _profileRes = MutableLiveData<ProfileResponse>()
    val profileRes : LiveData<ProfileResponse> = _profileRes

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _userSession = MutableLiveData<UserSession>()
    val userSession : LiveData<UserSession> = _userSession

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int> get() = _userId

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> get() = _isLogin

    init {
        getUserSession()
    }

    fun doLogout() {
        viewModelScope.launch {
            userPreference.logout()
        }
    }

    fun getProfile() {
        _isLoading.value = true

        Log.d("INI TOKEN", _token.value!!)
        Log.d("INI TOKEN", _userId.value.toString())

        val token = _token.value
        val userId = _userId.value

        if (token.isNullOrEmpty()) {
            Log.d("INI TOKEN2", _token.value!!)
            _isLoading.value = false
            _errorMessage.value = "Token atau UserId belum tersedia"
            Log.d("GET-PROFILE-ERROR", "Token atau UserId belum tersedia")
            return
        }

        val client = ApiConfig.getApiService().getProfile( token = "Bearer $token")
        client.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                Log.d("INI RESPONSE", response.body().toString())
                _isLoading.value = false
                if (response.isSuccessful && response.code() == 200) {
                    _profileRes.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        try {
                            val errorJson = JSONObject(it)
                            val errorMessage = errorJson.getString("error")
                            Log.d("GET-PROFILE-ERROR", "Error message: $errorMessage")
                            _errorMessage.value = errorMessage
                        }catch (e : JSONException){
                            Log.d("GET-PROFILE-ERROR", "Error message: $e")
                            _errorMessage.value = e.message
                        }
                    }
                    Log.d("GET-PROFILE-ERROR", "onFailure : ${response.message()}")
                    _errorMessage.value = response.message()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message
                Log.d("GET-PROFILE-ERROR", "onFailure: ${t.message}")
            }
        })
    }


    private fun getUserSession() {
        viewModelScope.launch {
            userPreference.getSession().collect() { userSession ->
                _token.postValue(userSession.token)
                _userId.postValue(userSession.userId)
                _isLogin.postValue(userSession.isLogin)
            }
        }
    }
}