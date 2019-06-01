package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import me.chipnesh.proceil.domain.enumeration.MeasureUnit
import io.github.jhipster.service.filter.Filter
import io.github.jhipster.service.filter.IntegerFilter
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.MaterialAvailabilityModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.MaterialAvailabilityResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/material-availabilities?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class MaterialAvailabilityCriteria(

    var id: LongFilter? = null,

    var availabilitySummary: StringFilter? = null,

    var remainingQuantity: IntegerFilter? = null,

    var measureUnit: MeasureUnitFilter? = null,

    var materialId: LongFilter? = null,

    var availableAtId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: MaterialAvailabilityCriteria) :
        this(
            other.id?.copy(),
            other.availabilitySummary?.copy(),
            other.remainingQuantity?.copy(),
            other.measureUnit?.copy(),
            other.materialId?.copy(),
            other.availableAtId?.copy()
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

    override fun copy(): MaterialAvailabilityCriteria {
        return MaterialAvailabilityCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
