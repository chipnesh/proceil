package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.IntegerFilter
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import io.github.jhipster.service.filter.InstantFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.ServiceAvailabilityModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.ServiceAvailabilityResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/service-availabilities?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class ServiceAvailabilityCriteria(

    var id: LongFilter? = null,

    var availabilitySummary: StringFilter? = null,

    var dateFrom: InstantFilter? = null,

    var dateTo: InstantFilter? = null,

    var remainingQuotas: IntegerFilter? = null,

    var serviceId: LongFilter? = null,

    var providedById: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: ServiceAvailabilityCriteria) :
        this(
            other.id?.copy(),
            other.availabilitySummary?.copy(),
            other.dateFrom?.copy(),
            other.dateTo?.copy(),
            other.remainingQuotas?.copy(),
            other.serviceId?.copy(),
            other.providedById?.copy()
        )

    override fun copy(): ServiceAvailabilityCriteria {
        return ServiceAvailabilityCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
