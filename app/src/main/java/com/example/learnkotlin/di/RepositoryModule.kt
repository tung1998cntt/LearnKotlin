package com.example.learnkotlin.di

import com.example.learnkotlin.data.repository.home.HomeRepositoryImpl
import com.example.learnkotlin.data.repository.profile.ProfileRepositoryImpl
import com.example.learnkotlin.data.repository.route.RouteRepositoryImpl
import com.example.learnkotlin.data.repository.user.UserRepositoryImpl
import com.example.learnkotlin.domain.repository.home.HomeRepository
import com.example.learnkotlin.domain.repository.profile.ProfileRepository
import com.example.learnkotlin.domain.repository.route.RouteRepository
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

    @Binds
    @Singleton
    abstract fun bindHomeRepository(homeRepoImpl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    abstract fun bindRouteRepository(routeRepositoryImpl: RouteRepositoryImpl): RouteRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository

}