package me.chipnesh.proceil.service.dto

import java.time.Instant
import java.io.Serializable
import java.util.Objects
import me.chipnesh.proceil.domain.enumeration.ServiceQuotingStatus

/**
 * A DTO for the [me.chipnesh.proceil.domain.ServiceQuotaModel] entity.
 */
data class ServiceQuotaValueObject(

    var id: Long? = null,

    var dateFrom: Instant? = null,

    var dateTo: Instant? = null,

    var quotaStatus: ServiceQuotingStatus? = null,

    var quantityToQuote: Int? = null,

    var serviceId: Long? = null,

    var serviceServiceName: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ServiceQuotaValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
