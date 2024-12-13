package com.dicoding.schedmate.ui.detail_task

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.UserSession
import com.dicoding.schedmate.data.response.DeleteTaskResponse
import com.dicoding.schedmate.data.response.DetailTaskResponse
import com.dicoding.schedmate.data.response.ListTaskResponse
import com.dicoding.schedmate.data.response.UpdateTaskResponse
import com.dicoding.schedmate.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uriToFile
import java.io.File

class DetailTaskViewModel (private val userPreference: UserPreference) : ViewModel() {
    private val _detailTaskRes = MutableLiveData<DetailTaskResponse>()
    val detailTaskRes : LiveData<DetailTaskResponse> = _detailTaskRes

    private val _deleteTask = MutableLiveData<DeleteTaskResponse>()
    val deleteTask : LiveData<DeleteTaskResponse> = _deleteTask

    private val _updateTaskRes = MutableLiveData<UpdateTaskResponse>()
    val updateTaskRes : LiveData<UpdateTaskResponse> = _updateTaskRes

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _userSession = MutableLiveData<UserSession>()
    val userSession : LiveData<UserSession> = _userSession

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    fun deleteTask(idTask: Int, idClass: Int) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().deleteTask(token = "Bearer ${_token.value}",  idClass = idClass, idTask = idTask)
        client.enqueue(object : Callback<DeleteTaskResponse> {
            override fun onResponse(
                call: Call<DeleteTaskResponse>,
                response: Response<DeleteTaskResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.code() == 200){
                    _deleteTask.value = response.body()
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

            override fun onFailure(call: Call<DeleteTaskResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message
                Log.d("GET-PROFILE-ERROR", "onFailure: ${t.message}")
            }

        })


    }

    fun updateTask(taskId: Int, startTime: String? = null, endTime: String? = null, progressStatus: String, file: MultipartBody.Part? = null) {

        _isLoading.value = true

        val progress = progressStatus.toRequestBody("text/plain".toMediaType())
        val start = startTime?.toRequestBody("text/plain".toMediaType())
        val end = endTime?.toRequestBody("text/plain".toMediaType())

        val client = ApiConfig.getApiService().updateTask( token = "Bearer ${_token.value}",
            taskId = taskId,
            file = file,
            progress = progress,
            startTime = start,
            endTime = end,
            )

        client.enqueue(object : Callback<UpdateTaskResponse> {
            override fun onResponse(
                call: Call<UpdateTaskResponse>,
                response: Response<UpdateTaskResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.code() == 201){
                    _updateTaskRes.value = response.body()
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

            override fun onFailure(call: Call<UpdateTaskResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message
                Log.d("GET-PROFILE-ERROR", "onFailure: ${t.message}")
            }

        })
    }

    fun getDetailTask(idClass : Int, idTask: Int, role: String) {
        _isLoading.value = true

        val client = if (role == "guru") {
            ApiConfig.getApiService().getDetailTask(token = "Bearer ${_token.value}", classId = idClass, taskId = idTask)
        } else  {
            ApiConfig.getApiService().getDetailTaskStudent(token = "Bearer ${_token.value}", taskId = idTask)
        }
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