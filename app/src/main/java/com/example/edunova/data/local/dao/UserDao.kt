package com.example.edunova.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.edunova.data.local.entity.UserEntity

/**
 * DAO pour les opérations User (Critère I.4 - Couche Data)
 * Gestion de la persistance locale des utilisateurs
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getLoggedInUser(): LiveData<UserEntity?>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUserSync(): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET authToken = :token, isLoggedIn = 1 WHERE id = :userId")
    suspend fun updateAuthToken(userId: Int, token: String)

    @Query("UPDATE users SET isLoggedIn = 0, authToken = NULL")
    suspend fun logoutAllUsers()

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
    
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>
}