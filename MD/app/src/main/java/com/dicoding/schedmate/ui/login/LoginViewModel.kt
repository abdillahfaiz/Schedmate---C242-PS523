package com.dicoding.schedmate.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.schedmate.data.pref.SessionManager
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.UserSession
import com.dicoding.schedmate.data.response.LoginResponse
import com.dicoding.schedmate.data.retrofit.ApiConfig
import com.dicoding.schedmate.data.retrofit.ApiService
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val userPreference: UserPreference): ViewModel() {

    private val _loginRes = MutableLiveData<LoginResponse>()
    val loginRes : LiveData<LoginResponse> = _loginRes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoding : LiveData<Boolean> = _isLoading

    private val _snackbarText =  MutableLiveData<String>()
    val snackBarText : LiveData<String> = _snackbarText



    fun doLogin (username : String, password : String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(username, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && response.code() == 200) {
                    response.body()?.let { body ->
                        Log.d("INI TOKEN 0", body.token)
                        val userSession = UserSession(token = body.token, role = body.role, userId = body.userId , isLogin = true, )
                        saveUserSession(userSession)
                    }
                    _loginRes.value = response.body()
                    _snackbarText.value = response.body()?.message

                }else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        try {
                            val errorJson = JSONObject(it)
                            val errorMessage = errorJson.getString("error")
                            Log.d("LOGIN-ERROR", "Error message: $errorMessage")
                            _snackbarText.value = errorMessage
                        }catch (e : JSONException){
                            _snackbarText.value = "Terjadi Kesalahan"
                        }
                    }
                    Log.d("LOGIN-ERROR", "onFailure : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.d("LOGIN-ERROR", "onFailure : ${t.message}")
                _snackbarText.value = t.message

            }

        })
    }

    private fun saveUserSession(userSession: UserSession) {
        // Gunakan Coroutine untuk memanggil fungsi suspend
        viewModelScope.launch {
            userPreference.saveSession(userSession)
        }
    }

}