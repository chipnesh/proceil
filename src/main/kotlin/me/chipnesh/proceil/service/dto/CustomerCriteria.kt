package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import io.github.jhipster.service.filter.InstantFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.CustomerModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.CustomerResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/customers?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class CustomerCriteria(

    var id: LongFilter? = null,

    var customerSummary: StringFilter? = null,

    var firstname: StringFilter? = null,

    var lastname: StringFilter? = null,

    var middlename: StringFilter? = null,

    var birthDate: InstantFilter? = null,

    var email: StringFilter? = null,

    var phone: StringFilter? = null,

    var address: StringFilter? = null,

    var feedbackId: LongFilter? = null,

    var measurementId: LongFilter? = null,

    var orderId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: CustomerCriteria) :
        this(
            other.id?.copy(),
            other.customerSummary?.copy(),
            other.firstname?.copy(),
            other.lastname?.copy(),
            other.middlename?.copy(),
            other.birthDate?.copy(),
            other.email?.copy(),
            other.phone?.copy(),
            other.address?.copy(),
            other.feedbackId?.copy(),
            other.measurementId?.copy(),
            other.orderId?.copy()
        )

    override fun copy(): CustomerCriteria {
        return CustomerCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
