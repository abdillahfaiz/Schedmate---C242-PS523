package com.dicoding.schedmate.data.response

import com.google.gson.annotations.SerializedName

data class DetailTaskResponse(

	@field:SerializedName("data")
	val data: DetailTaskData,

	@field:SerializedName("message")
	val message: String
)

data class DetailTaskData(

	@field:SerializedName("reminding_time")
	val remindingTime: String,

	@field:SerializedName("users")
	val usersWithProgress2: List<UsersWithProgress2Item>,

	@field:SerializedName("class_id")
	val classId: Int,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("task_id")
	val taskId: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("priority")
	val priority: Any,

	@field:SerializedName("created_by")
	val createdBy: Int,

	@field:SerializedName("mapel")
	val mapel: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("deadline")
	val deadline: String,

	@field:SerializedName("category")
	val category: String,

	@field:SerializedName("progress")
	val progress: String
)

data class UsersWithProgress2Item(

	@field:SerializedName("start_time")
	val startTime: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("user_name")
	val userName: String,

	@field:SerializedName("end_time")
	val endTime: String,

	@field:SerializedName("progress")
	val progress: String,

	@field:SerializedName("user_photo")
	val userPhoto: String,

	@field:SerializedName("upload_file")
	val uploadFile: String,

	@field:SerializedName("email")
	val email: String
)
