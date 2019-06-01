package me.chipnesh.proceil.service.dto

import java.time.Instant
import java.io.Serializable
import java.util.Objects
import javax.persistence.Lob
import me.chipnesh.proceil.domain.enumeration.OrderStatus

/**
 * A DTO for the [me.chipnesh.proceil.domain.CustomerOrderModel] entity.
 */
data class CustomerOrderValueObject(

    var id: Long? = null,

    var orderSummary: String? = null,

    var createdDate: Instant? = null,

    var deadlineDate: Instant? = null,

    var orderStatus: OrderStatus? = null,

    var orderPaid: Boolean? = null,

    @Lob
    var orderNote: String? = null,

    var managerId: Long? = null,

    var managerEmployeeName: String? = null,

    var customerId: Long? = null,

    var customerCustomerSummary: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CustomerOrderValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
