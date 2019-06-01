package me.chipnesh.proceil.security

import org.springframework.security.access.prepost.PreAuthorize
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import me.chipnesh.proceil.security.AuthoritiesConstants.ADMIN
import me.chipnesh.proceil.security.AuthoritiesConstants.MANAGER
import me.chipnesh.proceil.security.AuthoritiesConstants.WORKER
import me.chipnesh.proceil.security.AuthoritiesConstants.USER

@Target(FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, CLASS, FILE)
@Retention(RUNTIME)
@PreAuthorize("""hasAnyRole("$ADMIN", "$MANAGER")""")
annotation class ManagerAccess

@Target(FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, CLASS, FILE)
@Retention(RUNTIME)
@PreAuthorize("""hasAnyRole("$ADMIN", "$WORKER")""")
annotation class WorkerAccess

@Target(FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, CLASS, FILE)
@Retention(RUNTIME)
@PreAuthorize("""hasAnyRole("$ADMIN", "$USER", "$MANAGER")""")
annotation class UserAccess
