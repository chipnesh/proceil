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
import java.util.Objects

import me.chipnesh.proceil.domain.enumeration.MeasureUnit

/**
 * A MaterialAvailabilityModel.
 */
@Entity
@Table(name = "material_availability")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class MaterialAvailabilityModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "availability_summary")
    var availabilitySummary: String? = null,

    @Column(name = "remaining_quantity")
    var remainingQuantity: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "measure_unit")
    var measureUnit: MeasureUnit? = null,

    @ManyToOne
    @JsonIgnoreProperties("materialAvailabilities")
    var material: MaterialModel? = null,

    @ManyToOne
    @JsonIgnoreProperties("materials")
    var availableAt: ZoneModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaterialAvailabilityModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "MaterialAvailabilityModel{" +
            "id=$id" +
            ", availabilitySummary='$availabilitySummary'" +
            ", remainingQuantity=$remainingQuantity" +
            ", measureUnit='$measureUnit'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
