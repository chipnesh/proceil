package me.chipnesh.proceil.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

import java.io.Serializable
import java.time.Instant
import java.util.Objects

/**
 * A ServiceAvailabilityModel.
 */
@Entity
@Table(name = "service_availability")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class ServiceAvailabilityModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "availability_summary")
    var availabilitySummary: String? = null,

    @Column(name = "date_from")
    var dateFrom: Instant? = null,

    @Column(name = "date_to")
    var dateTo: Instant? = null,

    @Column(name = "remaining_quotas")
    var remainingQuotas: Int? = null,

    @ManyToOne
    @JsonIgnoreProperties("serviceAvailabilities")
    var service: ServiceModel? = null,

    @ManyToOne
    @JsonIgnoreProperties("services")
    var providedBy: ZoneModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ServiceAvailabilityModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "ServiceAvailabilityModel{" +
            "id=$id" +
            ", availabilitySummary='$availabilitySummary'" +
            ", dateFrom='$dateFrom'" +
            ", dateTo='$dateTo'" +
            ", remainingQuotas=$remainingQuotas" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
