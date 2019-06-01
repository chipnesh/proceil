package me.chipnesh.proceil.service.dto

import java.io.Serializable
import java.math.BigDecimal
import java.util.Objects

/**
 * A DTO for the [me.chipnesh.proceil.domain.ServiceModel] entity.
 */
data class ServiceValueObject(

    var id: Long? = null,

    var serviceName: String? = null,

    var serviceDescription: String? = null,

    var servicePrice: BigDecimal? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ServiceValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
