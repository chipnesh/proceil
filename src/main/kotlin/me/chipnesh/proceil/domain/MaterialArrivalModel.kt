package me.chipnesh.proceil.domain

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
 * A MaterialArrivalModel.
 */
@Entity
@Table(name = "material_arrival")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@EntityListeners(AuditingEntityListener::class)
class MaterialArrivalModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "arrival_summary")
    var arrivalSummary: String? = null,

    @Column(name = "arrival_date")
    @CreatedDate
    var arrivalDate: Instant? = null,

    @Lob
    @Column(name = "arrival_note")
    var arrivalNote: String? = null,

    @Column(name = "arrived_quantity")
    var arrivedQuantity: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "measure_unit")
    var measureUnit: MeasureUnit? = null,

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    var request: MaterialRequestModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaterialArrivalModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "MaterialArrivalModel{" +
            "id=$id" +
            ", arrivalSummary='$arrivalSummary'" +
            ", arrivalDate='$arrivalDate'" +
            ", arrivalNote='$arrivalNote'" +
            ", arrivedQuantity=$arrivedQuantity" +
            ", measureUnit='$measureUnit'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
