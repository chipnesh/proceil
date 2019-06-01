package me.chipnesh.proceil.service.dto

import java.io.Serializable
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter

/**
 * Criteria class for the [me.chipnesh.proceil.domain.EmployeeModel] entity. This class is used in
 * [me.chipnesh.proceil.web.rest.EmployeeResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/employees?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class EmployeeCriteria(

    var id: LongFilter? = null,

    var employeeName: StringFilter? = null,

    var phone: StringFilter? = null,

    var measurementId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: EmployeeCriteria) :
        this(
            other.id?.copy(),
            other.employeeName?.copy(),
            other.phone?.copy(),
            other.measurementId?.copy()
        )

    override fun copy(): EmployeeCriteria {
        return EmployeeCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
