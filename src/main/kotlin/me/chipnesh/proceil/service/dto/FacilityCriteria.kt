package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.FacilityModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.FacilityResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/facilities?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class FacilityCriteria(

    var id: LongFilter? = null,

    var facilityName: StringFilter? = null,

    var zoneId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: FacilityCriteria) :
        this(
            other.id?.copy(),
            other.facilityName?.copy(),
            other.zoneId?.copy()
        )

    override fun copy(): FacilityCriteria {
        return FacilityCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
