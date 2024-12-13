package com.dicoding.schedmate.data.response

import com.google.gson.annotations.SerializedName

data class LeaderboardResponse(

	@field:SerializedName("data")
	val data: List<LeaderboardItem>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class LeaderboardItem(

	@field:SerializedName("total_time_hours")
	val totalTimeHours: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("total_time_diff_seconds")
	val totalTimeDiffSeconds: String,

	@field:SerializedName("total_tasks")
	val totalTasks: Int,

	@field:SerializedName("photo")
	val photo: String,

	@field:SerializedName("username")
	val username: String
)
