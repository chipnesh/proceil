package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.Authority
import me.chipnesh.proceil.domain.UserModel
import me.chipnesh.proceil.service.dto.UserValueObject

import org.springframework.stereotype.Service

/**
 * Mapper for the entity [UserModel] and its DTO called [UserValueObject].
 *
 * Normal mappers are generated using MapStruct, this one is hand-coded as MapStruct
 * support is still in beta, and requires a manual step with an IDE.
 */
@Service
class UserMapper {

    fun usersToUserDTOs(users: List<UserModel?>): MutableList<UserValueObject> {
        return users.asSequence()
            .filterNotNull()
            .mapTo(mutableListOf()) { this.userToUserDTO(it) }
    }

    fun userToUserDTO(user: UserModel): UserValueObject {
        return UserValueObject(user)
    }

    fun userDTOsToUsers(userDTOs: List<UserValueObject?>): MutableList<UserModel> {
        return userDTOs.asSequence()
            .map { userDTOToUser(it) }
            .filterNotNullTo(mutableListOf())
    }

    fun userDTOToUser(userDTO: UserValueObject?): UserModel? {
        return when (userDTO) {
            null -> null
            else -> {
                val user = UserModel()
                user.id = userDTO.id
                user.login = userDTO.login
                user.firstName = userDTO.firstName
                user.lastName = userDTO.lastName
                user.email = userDTO.email
                user.imageUrl = userDTO.imageUrl
                user.activated = userDTO.activated
                user.langKey = userDTO.langKey
                user.authorities = authoritiesFromStrings(userDTO.authorities)
                user
            }
        }
    }

    private fun authoritiesFromStrings(authoritiesAsString: Set<String>?): MutableSet<Authority> {
        return authoritiesAsString?.mapTo(mutableSetOf()) {
            val auth = Authority()
            auth.name = it
            auth
        } ?: mutableSetOf()
    }

    fun userFromId(id: Long?): UserModel? {
        if (id == null) {
            return null
        }
        val user = UserModel()
        user.id = id
        return user
    }
}
