package com.dicoding.schedmate.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("role")
	val role: String,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("userId")
	val userId: Int,

	@field:SerializedName("token")
	val token: String
)
