package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import me.chipnesh.proceil.domain.enumeration.MaterialReserveStatus
import me.chipnesh.proceil.domain.enumeration.MeasureUnit
import io.github.jhipster.service.filter.Filter
import io.github.jhipster.service.filter.IntegerFilter
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.InstantFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.MaterialReserveModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.MaterialReserveResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/material-reserves?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class MaterialReserveCriteria(

    var id: LongFilter? = null,

    var reserveDate: InstantFilter? = null,

    var reserveStatus: MaterialReserveStatusFilter? = null,

    var quantityToReserve: IntegerFilter? = null,

    var measureUnit: MeasureUnitFilter? = null,

    var materialId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: MaterialReserveCriteria) :
        this(
            other.id?.copy(),
            other.reserveDate?.copy(),
            other.reserveStatus?.copy(),
            other.quantityToReserve?.copy(),
            other.measureUnit?.copy(),
            other.materialId?.copy()
        )

    /**
     * Class for filtering MaterialReserveStatus
     */
    class MaterialReserveStatusFilter : Filter<MaterialReserveStatus> {
        constructor()

        constructor(filter: MaterialReserveStatusFilter) : super(filter)

        override fun copy(): MaterialReserveStatusFilter {
            return MaterialReserveStatusFilter(this)
        }
    }

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

    override fun copy(): MaterialReserveCriteria {
        return MaterialReserveCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
