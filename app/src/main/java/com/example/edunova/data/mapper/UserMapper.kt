package com.example.edunova.data.mapper

import com.example.edunova.data.local.entity.UserEntity
import com.example.edunova.data.model.User

/**
 * Mapper pour convertir entre User (API) et UserEntity (Room)
 * Critère I.4 - Couche Data : Abstraction des sources de données
 */
object UserMapper {

    fun toEntity(user: User, authToken: String? = null, isLoggedIn: Boolean = false): UserEntity {
        return UserEntity(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            role = user.role,
            isVerified = user.isVerified,
            authToken = authToken,
            isLoggedIn = isLoggedIn,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }

    fun toModel(entity: UserEntity): User {
        return User(
            id = entity.id,
            firstName = entity.firstName,
            lastName = entity.lastName,
            email = entity.email,
            passwordHash = null,
            role = entity.role,
            googleId = null,
            isActive = true,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            resetPasswordToken = null,
            resetPasswordExpires = null,
            lastLogin = null,
            verificationToken = null,
            verificationTokenExpires = null,
            isVerified = entity.isVerified
        )
    }

    fun toEntityList(users: List<User>): List<UserEntity> {
        return users.map { toEntity(it) }
    }

    fun toModelList(entities: List<UserEntity>): List<User> {
        return entities.map { toModel(it) }
    }
}