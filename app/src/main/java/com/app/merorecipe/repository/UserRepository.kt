package com.app.merorecipe.repository

import com.app.merorecipe.api.APIRequest
import com.app.merorecipe.api.IUser
import com.app.merorecipe.api.ServiceBuilder
import com.app.merorecipe.entity.User
import com.app.merorecipe.response.ImageResponse
import com.app.merorecipe.response.LogoutResponse
import com.app.merorecipe.response.ProfileResponse
import com.app.merorecipe.response.UserResponse
import okhttp3.MultipartBody

class UserRepository : APIRequest() {

    private val userAPI = ServiceBuilder.buildService(IUser::class.java);

    suspend fun register(user: User): UserResponse {
        return apiRequest {
            userAPI.register(user);
        }
    }

    suspend fun uploadImage(id: String, body: MultipartBody.Part): ImageResponse {
        return apiRequest {
            userAPI.uploadImage(id, body);
        }
    }

    suspend fun login(username: String, password: String): UserResponse {
        return apiRequest {
            userAPI.login(username, password)
        }
    }

    suspend fun profile(id: String): ProfileResponse {
        return apiRequest {
            userAPI.profile(ServiceBuilder.token!!, id);
        }
    }

    suspend fun updateMe(user: User, id: String): UserResponse {
        return apiRequest {
            userAPI.update(ServiceBuilder.token!!, user, id);
        }
    }

    suspend fun updateImage(id: String, body: MultipartBody.Part): ImageResponse {
        return apiRequest {
            userAPI.updateImage(ServiceBuilder.token!!, id, body);
        }
    }

    suspend fun changePassword(id: String, user: User): UserResponse {
        return apiRequest {
            userAPI.changePassword(ServiceBuilder.token!!, id, user);
        }
    }

    suspend fun logout(): LogoutResponse {
        return apiRequest {
            userAPI.logout(ServiceBuilder.token!!);
        }
    }
}