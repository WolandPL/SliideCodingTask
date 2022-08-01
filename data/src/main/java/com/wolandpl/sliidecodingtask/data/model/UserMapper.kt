package com.wolandpl.sliidecodingtask.data.model

import com.wolandpl.sliidecodingtask.domain.User
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun apiToDomain(apiUser: ApiUser): User {
        var name = apiUser.name
        val cutPoint = name.indexOfLast { it == '#' }

        var creationTime = -1L

        if (cutPoint != -1) {
            creationTime = name.substring(cutPoint + 1).toLongOrNull() ?: -1
            name = name.substring(0, cutPoint)
        }

        return User(
            id = apiUser.id,
            name = name,
            email = apiUser.email,
            creationTime = creationTime
        )
    }
}
