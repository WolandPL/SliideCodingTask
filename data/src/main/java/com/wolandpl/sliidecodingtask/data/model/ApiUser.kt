package com.wolandpl.sliidecodingtask.data.model

import androidx.annotation.Keep

@Keep
data class ApiUser(
    val id: String = "",
    val name: String,
    val email: String,
    val gender: String = "male",
    val status: String = "active"
)
