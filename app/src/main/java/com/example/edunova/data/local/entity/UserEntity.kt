package com.example.edunova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité Room pour la table User (Critère I.4 - Couche Data)
 * Stockage local des informations utilisateur et du token d'authentification
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val isVerified: Boolean = false,
    val authToken: String? = null, // Token d'authentification (Critère II.2 - Gestion de Session)
    val isLoggedIn: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)