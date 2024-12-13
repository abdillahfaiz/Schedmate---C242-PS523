package com.dicoding.schedmate.ui.home

import android.graphics.Paint.Join
import android.provider.ContactsContract.Profile
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.UserSession
import com.dicoding.schedmate.data.response.ClassResponse
import com.dicoding.schedmate.data.response.JoinClassResponse
import com.dicoding.schedmate.data.response.LoginResponse
import com.dicoding.schedmate.data.response.ProfileResponse
import com.dicoding.schedmate.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel (private val userPreference: UserPreference) : ViewModel() {

    private val _profileRes = MutableLiveData<ProfileResponse>()
    val profileRes : LiveData<ProfileResponse> = _profileRes

    private val _classRes = MutableLiveData<ClassResponse>()
    val classRes : LiveData<ClassResponse> = _classRes

    private val _joinClassRes = MutableLiveData<JoinClassResponse>()
    val joinClassRes : LiveData<JoinClassResponse> = _joinClassRes

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

    private val _errorClassData = MutableLiveData<String>()
    val errorClassData: LiveData<String> = _errorClassData

    init {
        getUserSession()
    }

    fun getProfile() {
        _isLoading.value = true

        Log.d("INI TOKEN 1", _token.value!!)

        val token = _token.value

        if (token.isNullOrEmpty()) {
            Log.d("INI TOKEN 2", _token.value!!)
            _isLoading.value = false
            _errorMessage.value = "Token atau UserId belum tersedia"
            Log.d("GET-PROFILE-ERROR", "Token atau UserId belum tersedia")
            return
        }

        Log.d("INI TOKEN 3", token)

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

    fun getClassData() {
        _isLoading.value = true
        _errorMessage.value = ""

        val client = ApiConfig.getApiService().getClass(token = "Bearer ${_token.value}")
        client.enqueue(object : Callback<ClassResponse> {
            override fun onResponse(call: Call<ClassResponse>, response: Response<ClassResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && response.code() == 200){
                    _classRes.value = response.body()
                }else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        try {
                            val errorJson = JSONObject(it)
                            val errorMessage = errorJson.getString("error")
                            Log.d("GET-PROFILE-ERROR", "Error message: $errorMessage")
                            _errorClassData.value = errorMessage
                        }catch (e : JSONException){
                            Log.d("GET-PROFILE-ERROR", "Error message: $e")
                            _errorClassData.value = e.message
                        }
                    }
                    Log.d("GET-PROFILE-ERROR", "onFailure : ${response.message()}")
                    _errorClassData.value = response.message()
                }
            }

            override fun onFailure(call: Call<ClassResponse>, t: Throwable) {
                _isLoading.value = false
                _errorClassData.value = t.message
                Log.d("GET-PROFILE-ERROR", "onFailure: ${t.message}")
            }

        })
    }

    fun doJoinClass(classCode: String) {
        _isLoading.value = true
        _errorMessage.value = ""

        val client = ApiConfig.getApiService().doJoinClass(token = "Bearer ${_token.value}", classCode = classCode)
        client.enqueue(object : Callback<JoinClassResponse> {
            override fun onResponse(
                call: Call<JoinClassResponse>,
                response: Response<JoinClassResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.code() == 200){
                    _joinClassRes.value = response.body()
                }else {
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

            override fun onFailure(call: Call<JoinClassResponse>, t: Throwable) {
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