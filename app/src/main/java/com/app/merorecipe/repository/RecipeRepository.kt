package com.app.merorecipe.repository

import com.app.merorecipe.api.APIRequest
import com.app.merorecipe.api.IRecipe
import com.app.merorecipe.api.ServiceBuilder
import com.app.merorecipe.entity.Recipe
import com.app.merorecipe.response.GetRecipeResponse
import com.app.merorecipe.response.ImageResponse
import com.app.merorecipe.response.ListRecipeResponse
import com.app.merorecipe.response.RecipeResponse
import okhttp3.MultipartBody

class RecipeRepository : APIRequest() {

    private val recipeAPI = ServiceBuilder.buildService(IRecipe::class.java);

    suspend fun addNewRecipe(recipe: Recipe): RecipeResponse {
        return apiRequest {
            recipeAPI.addNewRecipe(
                ServiceBuilder.token!!,
                recipe
            )
        }
    }

    suspend fun updateRecipe(recipeID: String, recipe: Recipe): RecipeResponse {
        return apiRequest {
            recipeAPI.updateRecipe(
                ServiceBuilder.token!!,
                recipeID,
                recipe
            )
        }
    }

    suspend fun updateImage(
        recipeID: String,
        body: MultipartBody.Part
    ): ImageResponse {
        return apiRequest {
            recipeAPI.updateImage(
                ServiceBuilder.token!!,
                recipeID,
                body
            )
        }
    }

    suspend fun uploadImage(
        recipeID: String,
        body: MultipartBody.Part
    ): ImageResponse {
        return apiRequest {
            recipeAPI.uploadImage(
                ServiceBuilder.token!!,
                recipeID,
                body
            )
        }
    }

    suspend fun deleteRecipe(recipeID: String): RecipeResponse {
        return apiRequest {
            recipeAPI.deleteRecipe(
                ServiceBuilder.token!!,
                recipeID
            )
        }
    }

    suspend fun getAllRecipe(): GetRecipeResponse {
        return apiRequest {
            recipeAPI.getAllRecipe(ServiceBuilder.token!!)
        }

    }

    suspend fun getDistinctCategory(username: String): ListRecipeResponse {
        return apiRequest {
            recipeAPI.getDistinctCategory(ServiceBuilder.token!!, username)
        }
    }

//    suspend fun recipePerCategory(category: String): GetRecipeResponse {
//        return apiRequest {
//            recipeAPI.recipePerCategory(ServiceBuilder.token!!, category)
//        }
//    }
//
//    suspend fun recipePerID(recipeID: String): RecipeResponse {
//        return apiRequest {
//            recipeAPI.recipePerID(ServiceBuilder.token!!, recipeID)
//        }
//    }

    suspend fun recipePerCategoryPerUser(username: String, category: String): GetRecipeResponse {
        return apiRequest {
            recipeAPI.recipePerCategoryPerUser(ServiceBuilder.token!!, username, category)
        }
    }

    suspend fun getFavRecipe(username: String, isFav: Boolean): GetRecipeResponse {
        return apiRequest {
            recipeAPI.getFavRecipe(ServiceBuilder.token!!, username, isFav)
        }
    }

    suspend fun updateToFav(id: String, username: String, recipe: Recipe): RecipeResponse {
        return apiRequest {
            recipeAPI.updateToFav(
                ServiceBuilder.token!!,
                id, username, recipe
            )
        }
    }
}