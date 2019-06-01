package me.chipnesh.proceil.service.dto

import javax.validation.constraints.Size
import java.io.Serializable
import java.util.Objects

/**
 * A DTO for the [me.chipnesh.proceil.domain.EmployeeModel] entity.
 */
data class EmployeeValueObject(

    var id: Long? = null,

    @get: Size(min = 1)
    var employeeName: String? = null,

    @get: Size(min = 1)
    var phone: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmployeeValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
