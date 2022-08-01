package com.wolandpl.sliidecodingtask.domain

import androidx.annotation.Keep

@Keep
data class User(
    val id: String = "",
    val name: String,
    val email: String,
    val creationTime: Long = -1
)
