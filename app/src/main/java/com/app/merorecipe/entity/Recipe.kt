package com.app.merorecipe.entity

data class Recipe(
    val _id: String? = null,
    val recipeName: String? = null,
    val addedBy: String? = null,
    val category: String? = null,
    val recipeDescription: String? = null,
    val photo: String? = null,
    var isFav: Boolean? = null,
    val favBy: String? = null,
    val createdAt: String? = null
)