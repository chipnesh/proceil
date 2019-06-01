package me.chipnesh.proceil.web.rest.vm

import me.chipnesh.proceil.service.dto.UserValueObject
import javax.validation.constraints.Size

/**
 * View Model extending the [UserValueObject], which is meant to be used in the user management UI.
 */
class ManagedUserVM : UserValueObject() {

    @field:Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    var password: String? = null

    override fun toString(): String {
        return "ManagedUserVM{" +
            "} " + super.toString()
    }

    companion object {
        const val PASSWORD_MIN_LENGTH: Int = 4
        const val PASSWORD_MAX_LENGTH: Int = 100
    }
}
