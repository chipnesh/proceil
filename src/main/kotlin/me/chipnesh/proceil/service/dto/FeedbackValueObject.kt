package me.chipnesh.proceil.service.dto

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import java.io.Serializable
import java.util.Objects
import javax.persistence.Lob

/**
 * A DTO for the [me.chipnesh.proceil.domain.FeedbackModel] entity.
 */
data class FeedbackValueObject(

    var id: Long? = null,

    @get: NotNull
    @get: Size(min = 1)
    var caption: String? = null,

    var email: String? = null,

    @Lob
    var text: String? = null,

    @Lob
    var feedbackResponse: String? = null,

    var authorId: Long? = null,

    var authorCustomerSummary: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FeedbackValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
