package me.chipnesh.proceil.service.dto

import io.swagger.annotations.ApiModel
import java.time.Instant
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import java.io.Serializable
import java.util.Objects

/**
 * A DTO for the [me.chipnesh.proceil.domain.CustomerModel] entity.
 */
@ApiModel(description = "Client")
data class CustomerValueObject(

    var id: Long? = null,

    var customerSummary: String? = null,

    @get: NotNull
    @get: Size(min = 1)
    var firstname: String? = null,

    @get: NotNull
    @get: Size(min = 1)
    var lastname: String? = null,

    var middlename: String? = null,

    var birthDate: Instant? = null,

    @get: NotNull
    @get: Size(min = 1)
    var email: String? = null,

    var phone: String? = null,

    var address: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CustomerValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
