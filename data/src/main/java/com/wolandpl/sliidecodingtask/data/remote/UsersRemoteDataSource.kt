package com.wolandpl.sliidecodingtask.data.remote

import com.wolandpl.sliidecodingtask.data.model.ApiUser

interface UsersRemoteDataSource {

    suspend fun getUsers(): Result<List<ApiUser>>

    suspend fun addUser(user: ApiUser): Result<ApiUser>

    suspend fun deleteUser(id: String): Result<Unit>
}
