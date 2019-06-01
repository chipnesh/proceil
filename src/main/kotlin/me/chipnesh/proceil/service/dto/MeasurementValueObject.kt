package me.chipnesh.proceil.service.dto

import java.time.Instant
import java.io.Serializable
import java.util.Objects
import javax.persistence.Lob

/**
 * A DTO for the [me.chipnesh.proceil.domain.MeasurementModel] entity.
 */
data class MeasurementValueObject(

    var id: Long? = null,

    var measurementSummary: String? = null,

    var measureDate: Instant? = null,

    @Lob
    var measureNote: String? = null,

    var measureAddress: String? = null,

    var workerId: Long? = null,

    var workerEmployeeName: String? = null,

    var clientId: Long? = null,

    var clientCustomerSummary: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MeasurementValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
