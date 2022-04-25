package com.app.merorecipe.response

import com.app.merorecipe.entity.Recipe

data class RecipeResponse(
    val success: Boolean? = null,
    val data: Recipe? = null
)