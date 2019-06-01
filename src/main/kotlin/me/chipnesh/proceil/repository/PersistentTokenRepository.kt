package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.PersistentToken
import me.chipnesh.proceil.domain.UserModel
import java.time.LocalDate
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring Data JPA repository for the [PersistentToken] entity.
 */
interface PersistentTokenRepository : JpaRepository<PersistentToken, String> {

    fun findByUser(user: UserModel): List<PersistentToken>

    fun findByTokenDateBefore(localDate: LocalDate): List<PersistentToken>
}
