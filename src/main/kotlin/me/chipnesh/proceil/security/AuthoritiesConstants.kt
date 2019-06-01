package me.chipnesh.proceil.security

/**
 * Constants for Spring Security authorities.
 */
object AuthoritiesConstants {

    const val ADMIN: String = "ROLE_ADMIN"

    const val USER: String = "ROLE_USER"
    const val MANAGER: String = "ROLE_MANAGER"
    const val WORKER: String = "ROLE_WORKER"

    const val ANONYMOUS: String = "ROLE_ANONYMOUS"
}
