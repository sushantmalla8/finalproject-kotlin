package com.app.merorecipe.response

import com.app.merorecipe.entity.Recipe

data class ListRecipeResponse(
    val success: Boolean? = null,
    val data: ArrayList<String>? = null
)