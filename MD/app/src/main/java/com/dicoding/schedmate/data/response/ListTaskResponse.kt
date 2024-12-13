package com.dicoding.schedmate.data.response

import com.google.gson.annotations.SerializedName

data class ListTaskResponse(

	@field:SerializedName("data")
	val data: List<TaskDataItem>,

	@field:SerializedName("message")
	val message: String
)

data class TaskDataItem(

	@field:SerializedName("class_id")
	val classId: Int,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("deadline")
	val deadline: String,

	@field:SerializedName("category")
	val category: String,

	@field:SerializedName("priority")
	val priority: Double,

	@field:SerializedName("created_by")
	val createdBy: Int,

	@field:SerializedName("mapel")
	val mapel: String,

	@field:SerializedName("progress")
	val progress: String
)
