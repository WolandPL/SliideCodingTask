package com.wolandpl.sliidecodingtask.ui.model

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wolandpl.sliidecodingtask.data.UsersRepository
import com.wolandpl.sliidecodingtask.data.model.ApiUser
import com.wolandpl.sliidecodingtask.data.model.UserMapper
import com.wolandpl.sliidecodingtask.domain.User
import com.wolandpl.sliidecodingtask.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class UsersViewModel @Inject constructor(
    application: Application,
    private val usersRepository: UsersRepository,
    private val userMapper: UserMapper
) : AndroidViewModel(application) {
    private val _users = mutableListOf<User>()

    var uiState by mutableStateOf(UserUiState())
        private set

    init {
        refresh()
    }

    private fun refresh() {
        uiState = uiState.copy(
            loading = true
        )

        performAndVerify(
            action = {
                usersRepository.getUsers()
            },
            onSuccess = {
                _users.clear()
                _users.addAll(
                    it.getOrNull()?.map { apiUser ->
                        userMapper.apiToDomain(apiUser)
                    } ?: emptyList()
                )

                uiState = uiState.copy(
                    users = _users.toList(),
                    error = null
                )
                println("WOLAND 4")

                uiState = uiState.copy(
                    loading = false
                )
            }
        )
    }

    fun addUser(name: String, email: String) {
        performAndVerify(
            action = {
                usersRepository.addUser(
                    ApiUser(
                        name = "$name#${Calendar.getInstance().time.time}",
                        email = email
                    )
                )
            },
            onSuccess = { addResult ->
                (addResult.getOrNull())?.let {
                    _users.add(0, userMapper.apiToDomain(it))

                    uiState = uiState.copy(
                        users = _users.toList()
                    )
                }
            }
        )
    }

    fun deleteUser(user: User) {
        performAndVerify(
            action = {
                usersRepository.deleteUser(user.id)
            },
            onSuccess = {
                _users.remove(user)

                if (_users.isEmpty()) {
                    refresh()
                } else {
                    uiState = uiState.copy(
                        users = _users.toList()
                    )
                }
            }
        )
    }

    private fun <T> performAndVerify(
        action: suspend () -> Result<T>,
        onSuccess: (Result<T>) -> Unit
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(
                loading = true
            )

            val result = action()

            if (result.isSuccess) {
                onSuccess(result)
            } else {
                showError(result)
            }

            uiState = uiState.copy(
                loading = false
            )
        }
    }

    fun errorShown() {
        uiState = uiState.copy(
            error = null
        )
    }

    private fun showError(result: Result<*>) {
        var message = getApplication<Application>().getString(R.string.something_went_wrong)

        result.exceptionOrNull()?.let {
            message += ":\n(${it.message?.trim()})"
        }

        uiState = uiState.copy(
            error = message
        )
    }
}
