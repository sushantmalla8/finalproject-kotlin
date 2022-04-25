package com.app.merorecipe.api

import com.app.merorecipe.entity.User
import com.app.merorecipe.response.ImageResponse
import com.app.merorecipe.response.LogoutResponse
import com.app.merorecipe.response.ProfileResponse
import com.app.merorecipe.response.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface IUser {

    @POST("/register")
    suspend fun register(
        @Body user: User
    ): Response<UserResponse>

    @Multipart
    @POST("/uploadProfileImage/{id}")
    suspend fun uploadImage(
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): Response<ImageResponse>

    @FormUrlEncoded
    @POST("/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<UserResponse>

    @GET("/profile/{id}")
    suspend fun profile(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ProfileResponse>

    @PUT("/update/{id}")
    suspend fun update(
        @Header("Authorization") token: String,
        @Body user: User,
        @Path("id") id: String
    ): Response<UserResponse>

    @Multipart
    @POST("/updateImage/{id}")
    suspend fun updateImage(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): Response<ImageResponse>

    @PUT("/updatePassword/{id}")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body user: User
    ): Response<UserResponse>

    @GET("/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<LogoutResponse>


}