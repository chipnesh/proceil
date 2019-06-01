package me.chipnesh.proceil.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

import java.io.Serializable
import java.time.Instant
import java.util.Objects

import me.chipnesh.proceil.domain.enumeration.ServiceQuotingStatus

/**
 * A ServiceQuotaModel.
 */
@Entity
@Table(name = "service_quota")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class ServiceQuotaModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "date_from")
    var dateFrom: Instant? = null,

    @Column(name = "date_to")
    var dateTo: Instant? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "quota_status")
    var quotaStatus: ServiceQuotingStatus? = null,

    @Column(name = "quantity_to_quote")
    var quantityToQuote: Int? = null,

    @ManyToOne
    @JsonIgnoreProperties("serviceQuotas")
    var service: ServiceModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ServiceQuotaModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "ServiceQuotaModel{" +
            "id=$id" +
            ", dateFrom='$dateFrom'" +
            ", dateTo='$dateTo'" +
            ", quotaStatus='$quotaStatus'" +
            ", quantityToQuote=$quantityToQuote" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
