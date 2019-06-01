package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import me.chipnesh.proceil.domain.enumeration.MeasureUnit
import io.github.jhipster.service.filter.Filter
import io.github.jhipster.service.filter.IntegerFilter
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import io.github.jhipster.service.filter.InstantFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.MaterialArrivalModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.MaterialArrivalResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/material-arrivals?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class MaterialArrivalCriteria(

    var id: LongFilter? = null,

    var arrivalSummary: StringFilter? = null,

    var arrivalDate: InstantFilter? = null,

    var arrivedQuantity: IntegerFilter? = null,

    var measureUnit: MeasureUnitFilter? = null,

    var requestId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: MaterialArrivalCriteria) :
        this(
            other.id?.copy(),
            other.arrivalSummary?.copy(),
            other.arrivalDate?.copy(),
            other.arrivedQuantity?.copy(),
            other.measureUnit?.copy(),
            other.requestId?.copy()
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

    override fun copy(): MaterialArrivalCriteria {
        return MaterialArrivalCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
