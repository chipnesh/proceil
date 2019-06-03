package me.chipnesh.proceil.domain

import me.chipnesh.proceil.domain.enumeration.MaterialRequestStatus
import me.chipnesh.proceil.domain.enumeration.MeasureUnit
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.Instant
import java.util.*
import javax.persistence.*

/**
 * Warehouse
 */
@Entity
@Table(name = "material_request")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@EntityListeners(AuditingEntityListener::class)
class MaterialRequestModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "request_summary")
    var requestSummary: String? = null,

    @Column(name = "created_date")
    @CreatedDate
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    var requester: FacilityModel? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
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
