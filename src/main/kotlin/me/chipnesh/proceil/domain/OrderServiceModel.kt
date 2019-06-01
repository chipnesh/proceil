package me.chipnesh.proceil.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

import java.io.Serializable
import java.time.Instant
import java.util.Objects

/**
 * A OrderServiceModel.
 */
@Entity
@Table(name = "order_service")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class OrderServiceModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "service_summary")
    var serviceSummary: String? = null,

    @Column(name = "created_date")
    var createdDate: Instant? = null,

    @Column(name = "service_date")
    var serviceDate: Instant? = null,

    @OneToOne
    @JoinColumn(unique = true)
    var quota: ServiceQuotaModel? = null,

    @ManyToOne
    @JsonIgnoreProperties("orderServices")
    var executor: EmployeeModel? = null,

    @ManyToOne
    @JsonIgnoreProperties("services")
    var order: CustomerOrderModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderServiceModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "OrderServiceModel{" +
            "id=$id" +
            ", serviceSummary='$serviceSummary'" +
            ", createdDate='$createdDate'" +
            ", serviceDate='$serviceDate'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
