package com.wolandpl.sliidecodingtask.ui

import android.app.Application
import com.wolandpl.sliidecodingtask.data.UsersRepository
import com.wolandpl.sliidecodingtask.data.model.ApiUser
import com.wolandpl.sliidecodingtask.data.model.UserMapper
import com.wolandpl.sliidecodingtask.ui.model.UsersViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.stubbing.Answer

@OptIn(ExperimentalCoroutinesApi::class)
class UsersViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    lateinit var viewModel: UsersViewModel

    private val dummyUser1 = ApiUser(
        name = "name1",
        email = "email1"
    )

    private val dummyUser2 = ApiUser(
        name = "name2",
        email = "email2"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        val applicationMock: Application =
            mock { }

        val repositoryMock: UsersRepository =
            mock {
                onBlocking { getUsers() } doReturn Result.success(
                    listOf(dummyUser1, dummyUser2)
                )

                onBlocking { addUser(any()) } doAnswer Answer { invocation ->
                    invocation.getArgument(0, ApiUser::class.java)
                }
            }

        viewModel = UsersViewModel(
            applicationMock,
            repositoryMock,
            UserMapper()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun refresh_fetchesDataFromRepository() = runTest {
        assert(viewModel.uiState.loading)

        advanceUntilIdle()

        assert(!viewModel.uiState.loading)
        assert(viewModel.uiState.users.size == 2)
    }

    @Test
    fun addUser_addsUserToRepository() = runTest {
        advanceUntilIdle()

        assert(viewModel.uiState.users.size == 2)

        viewModel.addUser("nam3", "email3")

        advanceUntilIdle()

        assert(viewModel.uiState.users.size == 3)
    }

    @Test
    fun deleteUser_deletesUserFromRepository() = runTest {
        advanceUntilIdle()

        assert(viewModel.uiState.users.size == 2)

        viewModel.deleteUser(UserMapper().apiToDomain(dummyUser1))

        advanceUntilIdle()

        assert(viewModel.uiState.users.size == 1)
    }
}
