package com.example.edunova.data.mapper

import com.example.edunova.data.local.entity.ProfileEntity
import com.example.edunova.data.model.Profile

/**
 * Mapper pour convertir entre Profile (API) et ProfileEntity (Room)
 * Critère I.4 - Couche Data : Abstraction des sources de données
 */
object ProfileMapper {

    fun toEntity(profile: Profile, isSynced: Boolean = true): ProfileEntity {
        return ProfileEntity(
            id = profile.id,
            firstName = profile.firstName,
            lastName = profile.lastName,
            email = profile.email,
            phone = profile.phone,
            address = profile.address,
            age = profile.age,
            profileImage = profile.profileImage,
            role = profile.role,
            level = profile.level,
            enrollmentYear = profile.enrollmentYear,
            specialty = profile.specialty,
            yearsExperience = profile.yearsExperience,
            createdAt = profile.createdAt,
            updatedAt = profile.updatedAt,
            isSynced = isSynced
        )
    }

    fun toModel(entity: ProfileEntity): Profile {
        return Profile(
            id = entity.id,
            firstName = entity.firstName,
            lastName = entity.lastName,
            email = entity.email,
            phone = entity.phone,
            address = entity.address,
            age = entity.age,
            profileImage = entity.profileImage,
            role = entity.role,
            level = entity.level,
            enrollmentYear = entity.enrollmentYear,
            specialty = entity.specialty,
            yearsExperience = entity.yearsExperience,
            emailVerifiedAt = null,
            rememberToken = null,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntityList(profiles: List<Profile>): List<ProfileEntity> {
        return profiles.map { toEntity(it) }
    }

    fun toModelList(entities: List<ProfileEntity>): List<Profile> {
        return entities.map { toModel(it) }
    }
}