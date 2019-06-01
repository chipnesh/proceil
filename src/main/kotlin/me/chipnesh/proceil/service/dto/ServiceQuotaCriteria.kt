package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import me.chipnesh.proceil.domain.enumeration.ServiceQuotingStatus
import io.github.jhipster.service.filter.Filter
import io.github.jhipster.service.filter.IntegerFilter
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.InstantFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.ServiceQuotaModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.ServiceQuotaResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/service-quotas?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class ServiceQuotaCriteria(

    var id: LongFilter? = null,

    var dateFrom: InstantFilter? = null,

    var dateTo: InstantFilter? = null,

    var quotaStatus: ServiceQuotingStatusFilter? = null,

    var quantityToQuote: IntegerFilter? = null,

    var serviceId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: ServiceQuotaCriteria) :
        this(
            other.id?.copy(),
            other.dateFrom?.copy(),
            other.dateTo?.copy(),
            other.quotaStatus?.copy(),
            other.quantityToQuote?.copy(),
            other.serviceId?.copy()
        )

    /**
     * Class for filtering ServiceQuotingStatus
     */
    class ServiceQuotingStatusFilter : Filter<ServiceQuotingStatus> {
        constructor()

        constructor(filter: ServiceQuotingStatusFilter) : super(filter)

        override fun copy(): ServiceQuotingStatusFilter {
            return ServiceQuotingStatusFilter(this)
        }
    }

    override fun copy(): ServiceQuotaCriteria {
        return ServiceQuotaCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
