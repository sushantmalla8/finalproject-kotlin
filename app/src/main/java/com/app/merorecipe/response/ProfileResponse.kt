package com.app.merorecipe.response

import com.app.merorecipe.entity.User

data class ProfileResponse(
    val success: Boolean? = null,
    val data: User? = null
)