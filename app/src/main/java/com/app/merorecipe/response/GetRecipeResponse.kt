package com.app.merorecipe.response

import com.app.merorecipe.entity.Recipe

data class GetRecipeResponse(
    val data: ArrayList<Recipe>,
    val success: Boolean? = null
)