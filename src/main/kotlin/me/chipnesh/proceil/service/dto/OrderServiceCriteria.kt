package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import io.github.jhipster.service.filter.InstantFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.OrderServiceModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.OrderServiceResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/order-services?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class OrderServiceCriteria(

    var id: LongFilter? = null,

    var serviceSummary: StringFilter? = null,

    var createdDate: InstantFilter? = null,

    var serviceDate: InstantFilter? = null,

    var quotaId: LongFilter? = null,

    var executorId: LongFilter? = null,

    var orderId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: OrderServiceCriteria) :
        this(
            other.id?.copy(),
            other.serviceSummary?.copy(),
            other.createdDate?.copy(),
            other.serviceDate?.copy(),
            other.quotaId?.copy(),
            other.executorId?.copy(),
            other.orderId?.copy()
        )

    override fun copy(): OrderServiceCriteria {
        return OrderServiceCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
