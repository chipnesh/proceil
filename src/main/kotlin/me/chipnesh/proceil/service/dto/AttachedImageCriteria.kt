package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.AttachedImageModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.AttachedImageResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/attached-images?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class AttachedImageCriteria(

    var id: LongFilter? = null,

    var imageName: StringFilter? = null,

    var materialId: LongFilter? = null,

    var serviceId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: AttachedImageCriteria) :
        this(
            other.id?.copy(),
            other.imageName?.copy(),
            other.materialId?.copy(),
            other.serviceId?.copy()
        )

    override fun copy(): AttachedImageCriteria {
        return AttachedImageCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
