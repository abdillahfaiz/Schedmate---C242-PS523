package com.dicoding.schedmate.data.retrofit

import com.dicoding.schedmate.data.response.ClassResponse
import com.dicoding.schedmate.data.response.CreateTaskResponse
import com.dicoding.schedmate.data.response.DeleteTaskResponse
import com.dicoding.schedmate.data.response.DetailTaskResponse
import com.dicoding.schedmate.data.response.JoinClassResponse
import com.dicoding.schedmate.data.response.LeaderboardResponse
import com.dicoding.schedmate.data.response.ListTaskResponse
import com.dicoding.schedmate.data.response.LoginResponse
import com.dicoding.schedmate.data.response.ProfileResponse
import com.dicoding.schedmate.data.response.RegisterResponse
import com.dicoding.schedmate.data.response.UpdateTaskResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("username") username: String,
        @Field("email") email : String,
        @Field("password") password : String,
        @Field("role") role : String
    ) : Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email : String,
        @Field("password") password: String
    ) : Call<LoginResponse>

    @GET("profile/100")
    fun getProfile(
        @Header("Authorization") token: String
    ): Call<ProfileResponse>

    @GET("class")
    fun getClass(
        @Header("Authorization") token: String
    ): Call<ClassResponse>

    @FormUrlEncoded
    @POST("join-class")
    fun doJoinClass(
        @Header("Authorization") token: String,
        @Field("classCode") classCode : String,
    ) : Call<JoinClassResponse>

    @GET("class/{classId}")
    fun getListTask(
        @Header("Authorization") token: String,
        @Path("classId") classId: Int
    ): Call<ListTaskResponse>

    @FormUrlEncoded
    @POST("class/{classId}/add-tasks")
    fun addTask(
        @Header("Authorization") token: String,
        @Path("classId") classId: Int,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("mapel") mapel: String,
        @Field("category") category: String,
        @Field("deadline") deadline: String,
    ) : Call<CreateTaskResponse>

    @GET("class/{classId}/tasks/{taskId}")
    fun getDetailTask(
        @Header("Authorization") token: String,
        @Path("classId") classId: Int,
        @Path("taskId") taskId: Int
    ): Call<DetailTaskResponse>

    @GET("tasks-murid/{taskId}")
    fun getDetailTaskStudent(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: Int
    ): Call<DetailTaskResponse>

    @Multipart
    @PUT("tasks-murid/{taskId}/update")
    fun updateTask(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: Int,
        @Part file: MultipartBody.Part?,
        @Part("progress") progress: RequestBody,
        @Part("start_time") startTime: RequestBody?,
        @Part("end_time") endTime: RequestBody?,
    ): Call<UpdateTaskResponse>

    @DELETE("class/{idClass}/tasks/{idTask}")
    fun deleteTask(
        @Header("Authorization") token: String,
        @Path("idClass") idClass: Int,
        @Path("idTask") idTask: Int
    ): Call<DeleteTaskResponse>

    @GET("leaderboard/{classId}")
    fun getLeaderboard(
        @Header("Authorization") token: String,
        @Path("classId") classId: Int
    ): Call<LeaderboardResponse>

}