package com.wolandpl.sliidecodingtask.data.di

import com.wolandpl.sliidecodingtask.data.remote.UsersApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object UsersApiServiceModule {

    // This binding is in a separate module because it's replaced in tests.
    @Provides
    fun provideUsersApiService(
        httpClient: OkHttpClient
    ): UsersApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://gorest.co.in/public/v2/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(UsersApiService::class.java)
    }
}
