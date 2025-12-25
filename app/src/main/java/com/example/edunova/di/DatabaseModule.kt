package com.example.edunova.di

import android.content.Context
import androidx.room.Room
import com.example.edunova.data.local.dao.CourseDao
import com.example.edunova.data.local.dao.ProfileDao
import com.example.edunova.data.local.dao.UserDao
import com.example.edunova.data.local.database.EduNovaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt pour l'injection de la base de données Room
 * Critère I.3 - Couche Repository : Injection de dépendances
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEduNovaDatabase(@ApplicationContext context: Context): EduNovaDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            EduNovaDatabase::class.java,
            "edunova_database"
        )
            .fallbackToDestructiveMigration() // Évite les crashes de migration
            .allowMainThreadQueries() // Temporaire pour éviter les crashes
            .build()
    }

    @Provides
    fun provideUserDao(database: EduNovaDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideCourseDao(database: EduNovaDatabase): CourseDao {
        return database.courseDao()
    }

    @Provides
    fun provideProfileDao(database: EduNovaDatabase): ProfileDao {
        return database.profileDao()
    }
}