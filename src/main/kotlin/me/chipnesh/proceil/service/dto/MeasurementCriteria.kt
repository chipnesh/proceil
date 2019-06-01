package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import io.github.jhipster.service.filter.InstantFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.MeasurementModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.MeasurementResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/measurements?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class MeasurementCriteria(

    var id: LongFilter? = null,

    var measurementSummary: StringFilter? = null,

    var measureDate: InstantFilter? = null,

    var measureAddress: StringFilter? = null,

    var materialsId: LongFilter? = null,

    var workerId: LongFilter? = null,

    var clientId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: MeasurementCriteria) :
        this(
            other.id?.copy(),
            other.measurementSummary?.copy(),
            other.measureDate?.copy(),
            other.measureAddress?.copy(),
            other.materialsId?.copy(),
            other.workerId?.copy(),
            other.clientId?.copy()
        )

    override fun copy(): MeasurementCriteria {
        return MeasurementCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
