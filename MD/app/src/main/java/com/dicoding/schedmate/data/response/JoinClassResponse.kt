package com.dicoding.schedmate.data.response

import com.google.gson.annotations.SerializedName

data class JoinClassResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("class")
	val jsonMemberClass: JsonMemberClass
)

data class JsonMemberClass(

	@field:SerializedName("code")
	val code: String,

	@field:SerializedName("name")
	val name: String
)
