package com.wolandpl.sliidecodingtask.data.remote

import com.wolandpl.sliidecodingtask.data.model.ApiUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UsersApiService {

    @GET("users")
    suspend fun getUsers(): List<ApiUser>

    @POST("users")
    suspend fun addUser(@Body user: ApiUser): ApiUser

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>
}
