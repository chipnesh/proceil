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

import me.chipnesh.proceil.domain.enumeration.MeasureUnit

/**
 * A MaterialArrivalModel.
 */
@Entity
@Table(name = "material_arrival")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class MaterialArrivalModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "arrival_summary")
    var arrivalSummary: String? = null,

    @Column(name = "arrival_date")
    var arrivalDate: Instant? = null,

    @Lob
    @Column(name = "arrival_note")
    var arrivalNote: String? = null,

    @Column(name = "arrived_quantity")
    var arrivedQuantity: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "measure_unit")
    var measureUnit: MeasureUnit? = null,

    @OneToOne
    @JoinColumn(unique = true)
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
