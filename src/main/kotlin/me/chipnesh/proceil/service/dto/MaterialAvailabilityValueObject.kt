package me.chipnesh.proceil.service.dto

import java.io.Serializable
import java.util.Objects
import me.chipnesh.proceil.domain.enumeration.MeasureUnit

/**
 * A DTO for the [me.chipnesh.proceil.domain.MaterialAvailabilityModel] entity.
 */
data class MaterialAvailabilityValueObject(

    var id: Long? = null,

    var availabilitySummary: String? = null,

    var remainingQuantity: Int? = null,

    var measureUnit: MeasureUnit? = null,

    var materialId: Long? = null,

    var materialMaterialName: String? = null,

    var availableAtId: Long? = null,

    var availableAtZoneName: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaterialAvailabilityValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
