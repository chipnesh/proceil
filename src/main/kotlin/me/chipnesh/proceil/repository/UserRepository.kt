package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.UserModel

import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import java.util.Optional
import java.time.Instant

/**
 * Spring Data JPA repository for the [UserModel] entity.
 */
@Repository
interface UserRepository : JpaRepository<UserModel, Long> {

    fun findOneByActivationKey(activationKey: String): Optional<UserModel>

    fun findAllByActivatedIsFalseAndCreatedDateBefore(dateTime: Instant): List<UserModel>

    fun findOneByResetKey(resetKey: String): Optional<UserModel>

    fun findOneByEmailIgnoreCase(email: String?): Optional<UserModel>

    fun findOneByLogin(login: String): Optional<UserModel>

    @EntityGraph(attributePaths = ["authorities"])
    fun findOneWithAuthoritiesById(id: Long): Optional<UserModel>

    @EntityGraph(attributePaths = ["authorities"])
    @Cacheable(cacheNames = [USERS_BY_LOGIN_CACHE])
    fun findOneWithAuthoritiesByLogin(login: String): Optional<UserModel>

    @EntityGraph(attributePaths = ["authorities"])
    @Cacheable(cacheNames = [USERS_BY_EMAIL_CACHE])
    fun findOneWithAuthoritiesByEmail(email: String): Optional<UserModel>

    fun findAllByLoginNot(pageable: Pageable, login: String): Page<UserModel>

    companion object {

        const val USERS_BY_LOGIN_CACHE: String = "usersByLogin"

        const val USERS_BY_EMAIL_CACHE: String = "usersByEmail"
    }
}
