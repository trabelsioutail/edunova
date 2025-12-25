package com.example.edunova.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.edunova.data.local.dao.CourseDao
import com.example.edunova.data.local.dao.ProfileDao
import com.example.edunova.data.local.dao.UserDao
import com.example.edunova.data.local.entity.CourseEntity
import com.example.edunova.data.local.entity.ProfileEntity
import com.example.edunova.data.local.entity.UserEntity

/**
 * Base de données Room principale (Critère I.4 - Couche Data)
 * Gestion de la persistance locale pour toutes les entités
 */
@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        ProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EduNovaDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: EduNovaDatabase? = null

        fun getDatabase(context: Context): EduNovaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EduNovaDatabase::class.java,
                    "edunova_database"
                )
                    .fallbackToDestructiveMigration() // Évite les crashes de migration
                    .allowMainThreadQueries() // Temporaire pour éviter les crashes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}