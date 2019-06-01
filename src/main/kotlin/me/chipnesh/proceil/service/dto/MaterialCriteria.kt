package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import io.github.jhipster.service.filter.BigDecimalFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.MaterialModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.MaterialResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/materials?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class MaterialCriteria(

    var id: LongFilter? = null,

    var materialName: StringFilter? = null,

    var materialDescription: StringFilter? = null,

    var materialPrice: BigDecimalFilter? = null,

    var imageId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: MaterialCriteria) :
        this(
            other.id?.copy(),
            other.materialName?.copy(),
            other.materialDescription?.copy(),
            other.materialPrice?.copy(),
            other.imageId?.copy()
        )

    override fun copy(): MaterialCriteria {
        return MaterialCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
