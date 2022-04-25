package com.app.merorecipe.response

import com.app.merorecipe.entity.User

data class UserResponse(
    val success: Boolean? = null,
    val data: User? = null,
    val token: String? = null
)