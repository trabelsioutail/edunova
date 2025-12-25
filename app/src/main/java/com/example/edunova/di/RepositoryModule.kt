package com.example.edunova.di

import com.example.edunova.data.local.dao.CourseDao
import com.example.edunova.data.local.dao.ProfileDao
import com.example.edunova.data.local.dao.UserDao
import com.example.edunova.data.remote.ApiService
import com.example.edunova.data.repository.AuthRepository
import com.example.edunova.data.repository.CourseRepository
import com.example.edunova.data.repository.ProfileRepository
import com.example.edunova.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt pour l'injection des repositories
 * Critère I.3 - Couche Repository : Injection de dépendances
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        userDao: UserDao
    ): AuthRepository {
        return AuthRepository(apiService, userDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: ApiService,
        userDao: UserDao
    ): UserRepository {
        return UserRepository(apiService, userDao)
    }

    @Provides
    @Singleton
    fun provideCourseRepository(
        apiService: ApiService,
        courseDao: CourseDao
    ): CourseRepository {
        return CourseRepository(apiService, courseDao)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        apiService: ApiService,
        profileDao: ProfileDao
    ): ProfileRepository {
        return ProfileRepository(apiService, profileDao)
    }
}