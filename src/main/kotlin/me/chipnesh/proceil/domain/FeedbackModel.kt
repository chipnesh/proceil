package me.chipnesh.proceil.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

import java.io.Serializable
import java.util.Objects

/**
 * A FeedbackModel.
 */
@Entity
@Table(name = "feedback")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class FeedbackModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @get: NotNull
    @get: Size(min = 1)
    @Column(name = "caption", nullable = false)
    var caption: String? = null,

    @Column(name = "email")
    var email: String? = null,

    @Lob
    @Column(name = "text", nullable = false)
    var text: String? = null,

    @Lob
    @Column(name = "feedback_response")
    var feedbackResponse: String? = null,

    @ManyToOne
    @JsonIgnoreProperties("feedbacks")
    var author: CustomerModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FeedbackModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "FeedbackModel{" +
            "id=$id" +
            ", caption='$caption'" +
            ", email='$email'" +
            ", text='$text'" +
            ", feedbackResponse='$feedbackResponse'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
