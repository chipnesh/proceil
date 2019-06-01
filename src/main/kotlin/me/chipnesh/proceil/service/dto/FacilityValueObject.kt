package me.chipnesh.proceil.service.dto

import java.io.Serializable
import java.util.Objects

/**
 * A DTO for the [me.chipnesh.proceil.domain.FacilityModel] entity.
 */
data class FacilityValueObject(

    var id: Long? = null,

    var facilityName: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FacilityValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
