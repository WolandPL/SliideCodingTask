package com.wolandpl.sliidecodingtask.data

import com.wolandpl.sliidecodingtask.data.model.ApiUser
import com.wolandpl.sliidecodingtask.data.remote.UsersRemoteDataSource
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val dataSource: UsersRemoteDataSource
) : UsersRepository {

    override suspend fun getUsers() = dataSource.getUsers()

    override suspend fun addUser(user: ApiUser) = dataSource.addUser(user)

    override suspend fun deleteUser(id: String) = dataSource.deleteUser(id)
}
