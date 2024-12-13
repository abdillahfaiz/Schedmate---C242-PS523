package com.dicoding.schedmate.ui.create_task

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.UserSession
import com.dicoding.schedmate.data.response.CreateTaskResponse
import com.dicoding.schedmate.data.response.ListTaskResponse
import com.dicoding.schedmate.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateTaskViewModel (private val userPreference: UserPreference) : ViewModel() {
    private val _createTaskRes = MutableLiveData<CreateTaskResponse>()
    val createTaskRes : LiveData<CreateTaskResponse> = _createTaskRes

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _userSession = MutableLiveData<UserSession>()
    val userSession : LiveData<UserSession> = _userSession

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    fun addTask(
        idClass : Int,
        title: String,
        description: String,
        deadline: String,
        category: String,
        mapel: String
    ) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().addTask(token = "Bearer ${_token.value}", classId = idClass,title, description, mapel, category, deadline )
        client.enqueue(object : Callback<CreateTaskResponse> {
            override fun onResponse(
                call: Call<CreateTaskResponse>,
                response: Response<CreateTaskResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.code() == 201){
                    _createTaskRes.value = response.body()
                    Log.d("CREATE-TASK", "onResponse: ${response.body()}")
                }else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        try {
                            val errorJson = JSONObject(it)
                            val errorMessage = errorJson.getString("error")
                            Log.e("ERROR 1", "Error message: $errorMessage")
                            _errorMessage.value = errorMessage
                        }catch (e : JSONException){
                            Log.d("GET-PROFILE-ERROR", "Error message: $e")
                            _errorMessage.value = e.message
                        }
                    }
                    Log.e("ERROR 2", "onFailure : ${response.message()}")
                    _errorMessage.value = response.message()
                }
            }

            override fun onFailure(call: Call<CreateTaskResponse>, t: Throwable) {
                _isLoading.value = false
//                _errorMessage.value = t.message
                Log.e("ERROR 3", "onFailure: ${t.message}")
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