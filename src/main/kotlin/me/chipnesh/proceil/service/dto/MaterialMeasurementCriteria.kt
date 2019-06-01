package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import me.chipnesh.proceil.domain.enumeration.MeasureUnit
import io.github.jhipster.service.filter.Filter
import io.github.jhipster.service.filter.IntegerFilter
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.MaterialMeasurementModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.MaterialMeasurementResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/material-measurements?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class MaterialMeasurementCriteria(

    var id: LongFilter? = null,

    var measurementSummary: StringFilter? = null,

    var measurementValue: IntegerFilter? = null,

    var measureUnit: MeasureUnitFilter? = null,

    var materialId: LongFilter? = null,

    var measurementId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: MaterialMeasurementCriteria) :
        this(
            other.id?.copy(),
            other.measurementSummary?.copy(),
            other.measurementValue?.copy(),
            other.measureUnit?.copy(),
            other.materialId?.copy(),
            other.measurementId?.copy()
        )

    /**
     * Class for filtering MeasureUnit
     */
    class MeasureUnitFilter : Filter<MeasureUnit> {
        constructor()

        constructor(filter: MeasureUnitFilter) : super(filter)

        override fun copy(): MeasureUnitFilter {
            return MeasureUnitFilter(this)
        }
    }

    override fun copy(): MaterialMeasurementCriteria {
        return MaterialMeasurementCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
