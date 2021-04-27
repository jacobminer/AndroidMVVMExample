package com.example.mvvmexample.di

import com.example.mvvmexample.cache.InMemoryCacheService
import com.example.mvvmexample.repository.CounterRepository
import com.example.mvvmexample.service.MockCounterService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * MVVMExample
 * Created by jake on 19/04/21, 3:29 PM
 */
@Module
@InstallIn(SingletonComponent::class) // Installs Module in the generate SingletonComponent.
internal object Module {
    @Provides
    fun provideExampleRepository(): CounterRepository {
        return CounterRepository(MockCounterService(), InMemoryCacheService())
    }
}