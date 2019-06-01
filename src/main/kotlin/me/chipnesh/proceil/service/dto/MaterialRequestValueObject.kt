package me.chipnesh.proceil.service.dto

import io.swagger.annotations.ApiModel
import java.time.Instant
import java.io.Serializable
import java.util.Objects
import javax.persistence.Lob
import me.chipnesh.proceil.domain.enumeration.MaterialRequestStatus
import me.chipnesh.proceil.domain.enumeration.MeasureUnit

/**
 * A DTO for the [me.chipnesh.proceil.domain.MaterialRequestModel] entity.
 */
@ApiModel(description = "Warehouse")
data class MaterialRequestValueObject(

    var id: Long? = null,

    var requestSummary: String? = null,

    var createdDate: Instant? = null,

    var closedDate: Instant? = null,

    @Lob
    var requestNote: String? = null,

    var requestPriority: Int? = null,

    var requestStatus: MaterialRequestStatus? = null,

    var requestedQuantity: Int? = null,

    var measureUnit: MeasureUnit? = null,

    var requesterId: Long? = null,

    var requesterFacilityName: String? = null,

    var materialId: Long? = null,

    var materialMaterialName: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaterialRequestValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
