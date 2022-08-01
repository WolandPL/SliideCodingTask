package com.wolandpl.sliidecodingtask.data.remote

import com.wolandpl.sliidecodingtask.data.model.ApiUser
import javax.inject.Inject
import retrofit2.HttpException

class UsersRemoteDataSourceImpl @Inject constructor(
    private val usersApiService: UsersApiService
) : UsersRemoteDataSource {

    override suspend fun getUsers(): Result<List<ApiUser>> =
        runCatching {
            usersApiService.getUsers()
        }

    override suspend fun addUser(user: ApiUser): Result<ApiUser> =
        runCatching {
            usersApiService.addUser(user)
        }

    override suspend fun deleteUser(id: String): Result<Unit> {
        val response = usersApiService.deleteUser(id)

        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(HttpException(response))
        }
    }
}
