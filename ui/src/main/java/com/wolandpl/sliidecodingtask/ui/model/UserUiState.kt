package com.wolandpl.sliidecodingtask.ui.model

import androidx.annotation.Keep
import com.wolandpl.sliidecodingtask.data.model.ApiUser
import com.wolandpl.sliidecodingtask.domain.User

@Keep
data class UserUiState(
    val loading: Boolean = true,
    val users: List<User> = emptyList(),
    val error: String? = null
)
