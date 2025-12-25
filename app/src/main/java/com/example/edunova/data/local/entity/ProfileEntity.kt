package com.example.edunova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité Room pour la table Profile (Critère I.4 - Couche Data)
 * Stockage local des profils utilisateur
 */
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val address: String?,
    val age: Int?,
    val profileImage: String?,
    val role: String,
    val level: String?,
    val enrollmentYear: Int?,
    val specialty: String?,
    val yearsExperience: Int?,
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false
)