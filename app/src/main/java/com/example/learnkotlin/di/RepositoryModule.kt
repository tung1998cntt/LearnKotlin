package com.example.learnkotlin.di

import com.example.learnkotlin.data.repository.user.UserRepositoryImpl
import com.example.learnkotlin.domain.repository.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepoImpl: UserRepositoryImpl): UserRepository
}