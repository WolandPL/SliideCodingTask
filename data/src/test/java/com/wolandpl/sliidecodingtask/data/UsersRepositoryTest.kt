package com.wolandpl.sliidecodingtask.data

import com.wolandpl.sliidecodingtask.data.model.ApiUser
import com.wolandpl.sliidecodingtask.data.remote.UsersRemoteDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking

class UsersRepositoryTest {

    private val dummyUser1 = ApiUser(
        name = "name1",
        email = "email1"
    )

    private val dummyUser2 = ApiUser(
        name = "name2",
        email = "email2"
    )

    @Test
    fun getUsers_returnsResultFromDataSource() {

        /* Given */
        val dataSource: UsersRemoteDataSource =
            mock {
                onBlocking { getUsers() } doReturn Result.success(
                    listOf(dummyUser1, dummyUser2)
                )
            }

        val repository = UsersRepositoryImpl(dataSource)

        /* When */
        val result = runBlocking {
            repository.getUsers()
        }

        /* Then */
        verifyBlocking(dataSource) { getUsers() }
        assertTrue(result.isSuccess)
        assertEquals(listOf(dummyUser1, dummyUser2), result.getOrNull())
    }

    @Test
    fun addUser_returnsResultFromDataSource() {

        /* Given */
        val dataSource: UsersRemoteDataSource =
            mock {
                onBlocking { addUser(dummyUser1) } doReturn Result.success(dummyUser1)
            }

        val repository = UsersRepositoryImpl(dataSource)

        /* When */
        val result = runBlocking {
            repository.addUser(dummyUser1)
        }

        /* Then */
        verifyBlocking(dataSource) { addUser(dummyUser1) }
        assertTrue(result.isSuccess)
        assertEquals(dummyUser1, result.getOrNull())
    }

    @Test
    fun deleteUser_returnsResultFromDataSource() {

        /* Given */
        val dummyUserId = "dummyUserId"
        val dataSource: UsersRemoteDataSource =
            mock {
                onBlocking { deleteUser(dummyUserId) } doReturn Result.failure(Exception())
            }

        val repository = UsersRepositoryImpl(dataSource)

        /* When */
        val result = runBlocking {
            repository.deleteUser(dummyUserId)
        }

        /* Then */
        verifyBlocking(dataSource) { deleteUser(dummyUserId) }
        assertTrue(result.isFailure)
    }
}
