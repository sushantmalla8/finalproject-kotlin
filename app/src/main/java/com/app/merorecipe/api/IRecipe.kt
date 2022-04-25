package com.app.merorecipe.api

import androidx.room.Update
import com.app.merorecipe.entity.Recipe
import com.app.merorecipe.response.GetRecipeResponse
import com.app.merorecipe.response.ImageResponse
import com.app.merorecipe.response.ListRecipeResponse
import com.app.merorecipe.response.RecipeResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface IRecipe {

    //Done
    @POST("/recipe/insert")
    suspend fun addNewRecipe(
        @Header("Authorization") token: String,
        @Body recipe: Recipe
    ): Response<RecipeResponse>

    @Multipart
    @POST("/recipe/uploads/{recipeId}")
    suspend fun updateImage(
        @Header("Authorization") token: String,
        @Path("recipeId") id: String,
        @Part file: MultipartBody.Part
    ): Response<ImageResponse>


    @Multipart
    @POST("/recipe/uploads/image/{recipeId}")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Path("recipeId") recipeId: String,
        @Part file: MultipartBody.Part
    ): Response<ImageResponse>

    @PUT("/recipe/update/{recipeID}")
    suspend fun updateRecipe(
        @Header("Authorization") token: String,
        @Path("recipeID") recipeID: String,
        @Body recipe: Recipe
    ): Response<RecipeResponse>


    @DELETE("/recipe/delete/{recipeID}")
    suspend fun deleteRecipe(
        @Header("Authorization") token: String,
        @Path("recipeID") recipeID: String
    ): Response<RecipeResponse>


    @GET("/recipe/getAll")
    suspend fun getAllRecipe(
        @Header("Authorization") token: String
    ): Response<GetRecipeResponse>


    @GET("/recipe/getDistinctCategory/{username}")
    suspend fun getDistinctCategory(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): Response<ListRecipeResponse>


    @GET("/recipe/{category}")
    suspend fun recipePerCategory(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<GetRecipeResponse>

    @GET("/recipe/{recipeID}")
    suspend fun recipePerID(
        @Header("Authorization") token: String,
        @Path("recipeID") recipeID: String
    ): Response<RecipeResponse>

    //Get recipe per category per user.. ( load in user profile)
    @GET("/recipe/{username}/{category}")
    suspend fun recipePerCategoryPerUser(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Path("category") category: String
    ): Response<GetRecipeResponse>

    @GET("/recipe/getfav/{username}/{isFav}")
    suspend fun getFavRecipe(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Path("isFav") isFav: Boolean
    ): Response<GetRecipeResponse>

    @PUT("/update/toFav/{id}/{username}")
    suspend fun updateToFav(
        @Header("Authorization") token: String,
        @Path("id") id: String, //Recipe ID
        @Path("username") username: String, //User ID
        @Body recipe: Recipe
    ): Response<RecipeResponse>


}