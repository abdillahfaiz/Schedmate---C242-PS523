package com.dicoding.schedmate.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.schedmate.data.response.RegisterResponse
import com.dicoding.schedmate.data.retrofit.ApiConfig
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse : LiveData<RegisterResponse> = _registerResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _snackbarText =  MutableLiveData<String>()
    val snackBarText : LiveData<String> = _snackbarText

    fun doRegister (username : String, email: String,  password : String, role : String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(username, email, password, role)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && response.code() == 201) {
                    _registerResponse.value = response.body()
                    _snackbarText.value = response.body()?.message
                }else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        try {
                            val errorJson = JSONObject(it)
                            val errorMessage = errorJson.getString("error")
                            Log.d("REGISTER-ERROR", "Error message: $errorMessage")
                            _snackbarText.value = errorMessage
                        }catch (e : JSONException){
                            _snackbarText.value = "Terjadi Kesalahan"
                        }
                    }
                    Log.d("REGISTER-ERROR", "onFailure : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.d("REGISTER-ERROR", "onFailure : ${t.message}")
                _snackbarText.value = t.message

            }
        })
    }

}