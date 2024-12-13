package com.dicoding.schedmate.data.response

import com.google.gson.annotations.SerializedName

data class ClassResponse(

	@field:SerializedName("data")
	val data: List<ClassDataItem>,

	@field:SerializedName("message")
	val message: String
)

data class ClassDataItem(

	@field:SerializedName("code_student")
	val code: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("name")
	val name: String
)
