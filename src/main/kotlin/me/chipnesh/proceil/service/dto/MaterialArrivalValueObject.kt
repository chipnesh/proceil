package me.chipnesh.proceil.service.dto

import java.time.Instant
import java.io.Serializable
import java.util.Objects
import javax.persistence.Lob
import me.chipnesh.proceil.domain.enumeration.MeasureUnit

/**
 * A DTO for the [me.chipnesh.proceil.domain.MaterialArrivalModel] entity.
 */
data class MaterialArrivalValueObject(

    var id: Long? = null,

    var arrivalSummary: String? = null,

    var arrivalDate: Instant? = null,

    @Lob
    var arrivalNote: String? = null,

    var arrivedQuantity: Int? = null,

    var measureUnit: MeasureUnit? = null,

    var requestId: Long? = null,

    var requestRequestSummary: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaterialArrivalValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
