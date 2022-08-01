package com.wolandpl.sliidecodingtask.data.di

import com.wolandpl.sliidecodingtask.data.UsersRepository
import com.wolandpl.sliidecodingtask.data.UsersRepositoryImpl
import com.wolandpl.sliidecodingtask.data.remote.UsersApiService
import com.wolandpl.sliidecodingtask.data.remote.UsersRemoteDataSource
import com.wolandpl.sliidecodingtask.data.remote.UsersRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun provideUsersRepository(
        usersRepositoryImpl: UsersRepositoryImpl
    ): UsersRepository

    @Binds
    abstract fun provideUsersRemoteDataSource(
        usersRemoteDataSourceImpl: UsersRemoteDataSourceImpl
    ): UsersRemoteDataSource

    companion object {
        private const val accessToken =
            "ce659dd8962c127bfa523eab8cffb0cb5ea42ff1f554eb7ec7e680f1a5f80bbf"

        private const val TOTAL_PAGES_HEADER = "X-Pagination-Pages"
        private const val CURRENT_PAGE_HEADER = "X-Pagination-Page"
        private const val PAGE_QUERY_PARAM = "page"
        private const val GET_METHOD_NAME = "get"

        @Provides
        fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder().addHeader(
                    "Authorization",
                    "Bearer $accessToken"
                ).build()

                var response = chain.proceed(request)

                if (request.method().lowercase() == GET_METHOD_NAME) {
                    response.header(TOTAL_PAGES_HEADER)?.toIntOrNull()?.let { pages ->
                        val currentPage = response.header(CURRENT_PAGE_HEADER)?.toIntOrNull()
                        if (currentPage != pages) {
                            response.close()

                            val url = request.url().newBuilder()
                                .addQueryParameter(PAGE_QUERY_PARAM, pages.toString())
                                .build()

                            response = chain.proceed(
                                request.newBuilder()
                                    .url(url)
                                    .build()
                            )
                        }
                    }
                }

                response
            }.build()
    }
}
