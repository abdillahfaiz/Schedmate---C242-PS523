package com.dicoding.schedmate.ui.detail_task_student

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.UserSession
import com.dicoding.schedmate.data.response.DetailTaskResponse
import com.dicoding.schedmate.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailTaskStudentViewModelViewModel (private val userPreference: UserPreference) : ViewModel() {
    private val _detailTaskRes = MutableLiveData<DetailTaskResponse>()
    val detailTaskRes : LiveData<DetailTaskResponse> = _detailTaskRes

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _userSession = MutableLiveData<UserSession>()
    val userSession : LiveData<UserSession> = _userSession

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    fun getDetailTask(idClass : Int, idTask: Int) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getDetailTask(token = "Bearer ${_token.value}", classId = idClass, taskId = idTask)
        client.enqueue(object : Callback<DetailTaskResponse> {
            override fun onResponse(
                call: Call<DetailTaskResponse>,
                response: Response<DetailTaskResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.code() == 200){
                    _detailTaskRes.value = response.body()
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

            override fun onFailure(call: Call<DetailTaskResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message
                Log.d("GET-PROFILE-ERROR", "onFailure: ${t.message}")
            }

        })
    }

    init {
        getUserSession()
    }

    private fun getUserSession() {
        viewModelScope.launch {
            userPreference.getSession().collect() { userSession ->
                _token.postValue(userSession.token)
            }
        }
    }
}