package com.wolandpl.sliidecodingtask.data

import com.wolandpl.sliidecodingtask.data.model.ApiUser

interface UsersRepository {

    suspend fun getUsers(): Result<List<ApiUser>>

    suspend fun addUser(user: ApiUser): Result<ApiUser>

    suspend fun deleteUser(id: String): Result<Unit>
}
