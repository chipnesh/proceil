package me.chipnesh.proceil.service.dto

import java.time.Instant
import java.io.Serializable
import java.util.Objects
import me.chipnesh.proceil.domain.enumeration.MaterialReserveStatus
import me.chipnesh.proceil.domain.enumeration.MeasureUnit

/**
 * A DTO for the [me.chipnesh.proceil.domain.MaterialReserveModel] entity.
 */
data class MaterialReserveValueObject(

    var id: Long? = null,

    var reserveDate: Instant? = null,

    var reserveStatus: MaterialReserveStatus? = null,

    var quantityToReserve: Int? = null,

    var measureUnit: MeasureUnit? = null,

    var materialId: Long? = null,

    var materialMaterialName: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaterialReserveValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
