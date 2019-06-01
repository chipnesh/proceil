package me.chipnesh.proceil.service.dto

import java.time.Instant
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import java.io.Serializable
import java.util.Objects
import me.chipnesh.proceil.domain.enumeration.MeasureUnit

/**
 * A DTO for the [me.chipnesh.proceil.domain.OrderMaterialModel] entity.
 */
data class OrderMaterialValueObject(

    var id: Long? = null,

    var materialSummary: String? = null,

    var createdDate: Instant? = null,

    @get: NotNull
    @get: Min(value = 0)
    var materialQuantity: Int? = null,

    var measureUnit: MeasureUnit? = null,

    var reserveId: Long? = null,

    var reserveReserveStatus: String? = null,

    var orderId: Long? = null,

    var orderOrderSummary: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderMaterialValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
