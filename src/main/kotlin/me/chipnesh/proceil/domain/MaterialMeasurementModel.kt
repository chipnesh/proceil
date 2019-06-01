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
 * A MaterialMeasurementModel.
 */
@Entity
@Table(name = "material_measurement")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class MaterialMeasurementModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "measurement_summary")
    var measurementSummary: String? = null,

    @Column(name = "measurement_value")
    var measurementValue: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "measure_unit")
    var measureUnit: MeasureUnit? = null,

    @ManyToOne
    @JsonIgnoreProperties("materialMeasurements")
    var material: MaterialModel? = null,

    @ManyToOne
    @JsonIgnoreProperties("materials")
    var measurement: MeasurementModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaterialMeasurementModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "MaterialMeasurementModel{" +
            "id=$id" +
            ", measurementSummary='$measurementSummary'" +
            ", measurementValue=$measurementValue" +
            ", measureUnit='$measureUnit'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
