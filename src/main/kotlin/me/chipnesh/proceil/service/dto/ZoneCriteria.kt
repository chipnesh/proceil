package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.ZoneModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.ZoneResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/zones?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class ZoneCriteria(

    var id: LongFilter? = null,

    var zoneName: StringFilter? = null,

    var materialId: LongFilter? = null,

    var serviceId: LongFilter? = null,

    var facilityId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: ZoneCriteria) :
        this(
            other.id?.copy(),
            other.zoneName?.copy(),
            other.materialId?.copy(),
            other.serviceId?.copy(),
            other.facilityId?.copy()
        )

    override fun copy(): ZoneCriteria {
        return ZoneCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
