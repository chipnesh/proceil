package me.chipnesh.proceil.service.dto

import java.time.Instant
import java.io.Serializable
import java.util.Objects

/**
 * A DTO for the [me.chipnesh.proceil.domain.OrderServiceModel] entity.
 */
data class OrderServiceValueObject(

    var id: Long? = null,

    var serviceSummary: String? = null,

    var createdDate: Instant? = null,

    var serviceDate: Instant? = null,

    var quotaId: Long? = null,

    var quotaQuotaStatus: String? = null,

    var executorId: Long? = null,

    var executorEmployeeName: String? = null,

    var orderId: Long? = null,

    var orderOrderSummary: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderServiceValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
