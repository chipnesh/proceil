package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import io.github.jhipster.service.filter.BigDecimalFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.ServiceModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.ServiceResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/services?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class ServiceCriteria(

    var id: LongFilter? = null,

    var serviceName: StringFilter? = null,

    var serviceDescription: StringFilter? = null,

    var servicePrice: BigDecimalFilter? = null,

    var imageId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: ServiceCriteria) :
        this(
            other.id?.copy(),
            other.serviceName?.copy(),
            other.serviceDescription?.copy(),
            other.servicePrice?.copy(),
            other.imageId?.copy()
        )

    override fun copy(): ServiceCriteria {
        return ServiceCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
