package me.chipnesh.proceil.service.dto

import java.time.Instant
import java.io.Serializable
import java.util.Objects

/**
 * A DTO for the [me.chipnesh.proceil.domain.ServiceAvailabilityModel] entity.
 */
data class ServiceAvailabilityValueObject(

    var id: Long? = null,

    var availabilitySummary: String? = null,

    var dateFrom: Instant? = null,

    var dateTo: Instant? = null,

    var remainingQuotas: Int? = null,

    var serviceId: Long? = null,

    var serviceServiceName: String? = null,

    var providedById: Long? = null,

    var providedByZoneName: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ServiceAvailabilityValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
