package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.FeedbackModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.FeedbackResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/feedbacks?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class FeedbackCriteria(

    var id: LongFilter? = null,

    var caption: StringFilter? = null,

    var email: StringFilter? = null,

    var authorId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: FeedbackCriteria) :
        this(
            other.id?.copy(),
            other.caption?.copy(),
            other.email?.copy(),
            other.authorId?.copy()
        )

    override fun copy(): FeedbackCriteria {
        return FeedbackCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
