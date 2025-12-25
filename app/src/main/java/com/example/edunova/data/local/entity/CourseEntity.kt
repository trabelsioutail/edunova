package com.example.edunova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité Room pour la table Course (Critère I.4 - Couche Data)
 * Cache local des cours pour améliorer les performances
 */
@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String?,
    val teacherId: Int,
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false // Indique si les données sont synchronisées avec le serveur
)