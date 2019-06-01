package me.chipnesh.proceil.service.dto

import java.io.Serializable
import java.math.BigDecimal
import java.util.Objects

/**
 * A DTO for the [me.chipnesh.proceil.domain.MaterialModel] entity.
 */
data class MaterialValueObject(

    var id: Long? = null,

    var materialName: String? = null,

    var materialDescription: String? = null,

    var materialPrice: BigDecimal? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaterialValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
