package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import me.chipnesh.proceil.domain.enumeration.OrderStatus
import io.github.jhipster.service.filter.BooleanFilter
import io.github.jhipster.service.filter.Filter
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import io.github.jhipster.service.filter.InstantFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.CustomerOrderModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.CustomerOrderResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/customer-orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class CustomerOrderCriteria(

    var id: LongFilter? = null,

    var orderSummary: StringFilter? = null,

    var createdDate: InstantFilter? = null,

    var deadlineDate: InstantFilter? = null,

    var orderStatus: OrderStatusFilter? = null,

    var orderPaid: BooleanFilter? = null,

    var materialsId: LongFilter? = null,

    var serviceId: LongFilter? = null,

    var managerId: LongFilter? = null,

    var customerId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: CustomerOrderCriteria) :
        this(
            other.id?.copy(),
            other.orderSummary?.copy(),
            other.createdDate?.copy(),
            other.deadlineDate?.copy(),
            other.orderStatus?.copy(),
            other.orderPaid?.copy(),
            other.materialsId?.copy(),
            other.serviceId?.copy(),
            other.managerId?.copy(),
            other.customerId?.copy()
        )

    /**
     * Class for filtering OrderStatus
     */
    class OrderStatusFilter : Filter<OrderStatus> {
        constructor()

        constructor(filter: OrderStatusFilter) : super(filter)

        override fun copy(): OrderStatusFilter {
            return OrderStatusFilter(this)
        }
    }

    override fun copy(): CustomerOrderCriteria {
        return CustomerOrderCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
