package com.wolandpl.sliidecodingtask.utils

import com.wolandpl.sliidecodingtask.utils.UsersApiServiceMock
import com.wolandpl.sliidecodingtask.data.di.UsersApiServiceModule
import com.wolandpl.sliidecodingtask.data.remote.UsersApiService
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [UsersApiServiceModule::class]
)
abstract class UsersApiServiceMockModule {

    @Singleton
    @Binds
    abstract fun bindUsersApiService(
        usersApiServiceMock: UsersApiServiceMock
    ): UsersApiService
}
