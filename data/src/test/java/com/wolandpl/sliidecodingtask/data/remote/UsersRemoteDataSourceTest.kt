package com.wolandpl.sliidecodingtask.data.remote

import com.wolandpl.sliidecodingtask.data.model.ApiUser
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import retrofit2.HttpException
import retrofit2.Response

class UsersRemoteDataSourceTest {

    private val dummyUser1 = ApiUser(
        name = "name1",
        email = "email1"
    )

    private val dummyUser2 = ApiUser(
        name = "name2",
        email = "email2"
    )

    private val dummyUserId = "dummyUserId"

    @Test
    fun getUsers_returnsResultFromApiService() {

        /* Given */
        val apiService: UsersApiService =
            mock {
                onBlocking { getUsers() } doReturn listOf(dummyUser1, dummyUser2)
            }

        val dataSource = UsersRemoteDataSourceImpl(apiService)

        /* When */
        val result = runBlocking {
            dataSource.getUsers()
        }

        /* Then */
        verifyBlocking(apiService) { getUsers() }
        assertTrue(result.isSuccess)
        assertEquals(result.getOrNull(), listOf(dummyUser1, dummyUser2))
    }

    @Test
    fun addUser_returnsResultFromApiService() {

        /* Given */
        val apiService: UsersApiService =
            mock {
                onBlocking { addUser(dummyUser1) } doReturn dummyUser1
            }

        val dataSource = UsersRemoteDataSourceImpl(apiService)

        /* When */
        val result = runBlocking {
            dataSource.addUser(dummyUser1)
        }

        /* Then */
        verifyBlocking(apiService) { addUser(dummyUser1) }
        assertTrue(result.isSuccess)
        assertEquals(result.getOrNull(), dummyUser1)
    }

    @Test
    fun allMethods_returnFailureFromApiService() {

        /* Given */
        val exception = RuntimeException()
        val dummyCode = 404
        val apiService: UsersApiService =
            mock {
                onBlocking { getUsers() } doThrow (exception)
                onBlocking { addUser(any()) } doThrow (exception)
                onBlocking { deleteUser(any()) } doReturn Response.error(
                    dummyCode,
                    ResponseBody.create(null, "")
                )
            }

        val dataSource = UsersRemoteDataSourceImpl(apiService)

        /* When */
        val getResult = runBlocking {
            dataSource.getUsers()
        }
        val addResult = runBlocking {
            dataSource.addUser(dummyUser1)
        }
        val deleteResult = runBlocking {
            dataSource.deleteUser(dummyUserId)
        }

        /* Then */
        verifyBlocking(apiService) { getUsers() }
        verifyBlocking(apiService) { addUser(dummyUser1) }
        verifyBlocking(apiService) { deleteUser(dummyUserId) }

        assertTrue(getResult.isFailure)
        assertTrue(addResult.isFailure)
        assertTrue(deleteResult.isFailure)

        assertEquals(exception, getResult.exceptionOrNull())
        assertEquals(exception, addResult.exceptionOrNull())

        assert(deleteResult.exceptionOrNull() is HttpException)
        assertEquals(dummyCode, (deleteResult.exceptionOrNull() as HttpException).code())
    }
}
