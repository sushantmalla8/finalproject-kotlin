package com.app.merorecipe.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

//@Entity(tableName = "User")
data class User(
    val _id: String? = null,
    val firstName: String? = null,
    val lastName:String?=null,
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val photo: String? = null,
    val gender:String?=null
)