package me.chipnesh.proceil.domain

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

import java.io.Serializable
import java.time.Instant
import java.util.Objects

import me.chipnesh.proceil.domain.enumeration.MaterialRequestStatus

import me.chipnesh.proceil.domain.enumeration.MeasureUnit

/**
 * Warehouse
 */
@Entity
@Table(name = "material_request")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class MaterialRequestModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "request_summary")
    var requestSummary: String? = null,

    @Column(name = "created_date")
    var createdDate: Instant? = null,

    @Column(name = "closed_date")
    var closedDate: Instant? = null,

    @Lob
    @Column(name = "request_note")
    var requestNote: String? = null,

    @Column(name = "request_priority")
    var requestPriority: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    var requestStatus: MaterialRequestStatus? = null,

    @Column(name = "requested_quantity")
    var requestedQuantity: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "measure_unit")
    var measureUnit: MeasureUnit? = null,

    @OneToOne
    @JoinColumn(unique = true)
    var requester: FacilityModel? = null,

    @OneToOne
    @JoinColumn(unique = true)
    var material: MaterialModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaterialRequestModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "MaterialRequestModel{" +
            "id=$id" +
            ", requestSummary='$requestSummary'" +
            ", createdDate='$createdDate'" +
            ", closedDate='$closedDate'" +
            ", requestNote='$requestNote'" +
            ", requestPriority=$requestPriority" +
            ", requestStatus='$requestStatus'" +
            ", requestedQuantity=$requestedQuantity" +
            ", measureUnit='$measureUnit'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
