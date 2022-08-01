package com.wolandpl.sliidecodingtask.utils

import com.wolandpl.sliidecodingtask.MainScreenTest.Companion.dummyUser1
import com.wolandpl.sliidecodingtask.MainScreenTest.Companion.dummyUser2
import com.wolandpl.sliidecodingtask.data.model.ApiUser
import com.wolandpl.sliidecodingtask.data.remote.UsersApiService
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.ResponseBody
import retrofit2.Response

@Singleton
class UsersApiServiceMock @Inject constructor() : UsersApiService {

    private val loadingStartedMutex = Mutex(true)

    private lateinit var loadLock: Continuation<List<ApiUser>>

    override suspend fun getUsers(): List<ApiUser> = suspendCoroutine { continuation ->
        loadLock = continuation
        loadingStartedMutex.unlock()
    }

    override suspend fun addUser(user: ApiUser): ApiUser = user

    override suspend fun deleteUser(id: String): Response<Unit> =
        Response.error(
            404,
            ResponseBody.create(null, "")
        )

    fun loadingFinished() {
        if (!this::loadLock.isInitialized) {
            runBlocking {
                loadingStartedMutex.withLock {}
            }
        }

        loadLock.resume(
            listOf(dummyUser1, dummyUser2)
        )
    }
}
