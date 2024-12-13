package com.dicoding.schedmate.ui.leaderboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.UserSession
import com.dicoding.schedmate.data.response.ClassResponse
import com.dicoding.schedmate.data.response.LeaderboardResponse
import com.dicoding.schedmate.data.response.ProfileResponse
import com.dicoding.schedmate.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaderboardViewModel (private val userPreference: UserPreference) : ViewModel() {
    private val _leaderboard = MutableLiveData<LeaderboardResponse>()
    val leaderboard: LiveData<LeaderboardResponse> = _leaderboard

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

    fun getLeaderboard(classId: Int) {

        _isLoading.value = true
        _errorMessage.value = ""

        val client = ApiConfig.getApiService().getLeaderboard(token = "Bearer ${_token.value}", classId = classId)
        client.enqueue(object : Callback<LeaderboardResponse> {
            override fun onResponse(
                call: Call<LeaderboardResponse>,
                response: Response<LeaderboardResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.code() == 200){
                    _leaderboard.value = response.body()
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

            override fun onFailure(call: Call<LeaderboardResponse>, t: Throwable) {
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