package com.dicoding.schedmate.data.response

import com.google.gson.annotations.SerializedName

data class CreateTaskResponse(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("message")
	val message: String
)

data class DataItem(

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("progress")
	val progress: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("priority")
	val priority: Int,

	@field:SerializedName("deadline")
	val deadline: String
)
