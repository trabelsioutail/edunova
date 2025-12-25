package com.example.edunova.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.edunova.data.local.entity.ProfileEntity

/**
 * DAO pour les opérations Profile (Critère I.4 - Couche Data)
 * CRUD complet pour la gestion des profils
 */
@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles ORDER BY firstName ASC")
    fun getAllProfiles(): LiveData<List<ProfileEntity>>

    @Query("SELECT * FROM profiles ORDER BY firstName ASC")
    suspend fun getAllProfilesSync(): List<ProfileEntity>

    @Query("SELECT * FROM profiles WHERE id = :profileId")
    suspend fun getProfileById(profileId: Long): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE email = :email")
    suspend fun getProfileByEmail(email: String): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE role = :role")
    fun getProfilesByRole(role: String): LiveData<List<ProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<ProfileEntity>)

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: ProfileEntity)

    @Query("DELETE FROM profiles WHERE id = :profileId")
    suspend fun deleteProfileById(profileId: Long)

    @Query("DELETE FROM profiles")
    suspend fun deleteAllProfiles()

    @Query("UPDATE profiles SET isSynced = :synced WHERE id = :profileId")
    suspend fun updateSyncStatus(profileId: Long, synced: Boolean)
}