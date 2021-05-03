package com.example.mvvmexample.di

import com.example.mvvmexample.BuildConfig
import com.example.mvvmexample.cache.CacheService
import com.example.mvvmexample.cache.InMemoryCacheService
import com.example.mvvmexample.repository.PostsRepository
import com.example.mvvmexample.repository.UsersRepository
import com.example.mvvmexample.service.PostsService
import com.example.mvvmexample.service.UsersService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * MVVMExample
 * Created by jake on 19/04/21, 3:29 PM
 */
@Module
@InstallIn(SingletonComponent::class) // Installs Module in the generate SingletonComponent.
internal object Module {
    @Singleton
    @Provides
    fun providesCacheService(): CacheService {
        return InMemoryCacheService()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        } else {
            OkHttpClient
                .Builder()
                .build()
        }
    }

    @Provides
    @Singleton
    fun providesJsonConverterFactory(): Converter.Factory {
        val contentType = "application/json".toMediaType()
        return Json {
            ignoreUnknownKeys = true
        }.asConverterFactory(contentType)
    }

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient, jsonConverterFactory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(jsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesUserService(retrofit: Retrofit): UsersService {
        return retrofit.create(UsersService::class.java)
    }

    @Provides
    @Singleton
    fun providesPostsService(retrofit: Retrofit): PostsService {
        return retrofit.create(PostsService::class.java)
    }

    @Provides
    @Singleton
    fun usersRepository(usersService: UsersService, cacheService: CacheService): UsersRepository {
        return UsersRepository(usersService, cacheService)
    }

    @Provides
    @Singleton
    fun postsRepository(postsService: PostsService, cacheService: CacheService): PostsRepository {
        return PostsRepository(postsService, cacheService)
    }
}