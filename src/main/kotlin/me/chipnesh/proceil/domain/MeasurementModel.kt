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
import javax.persistence.OneToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table

import java.io.Serializable
import java.time.Instant
import java.util.Objects

/**
 * A MeasurementModel.
 */
@Entity
@Table(name = "measurement")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class MeasurementModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "measurement_summary")
    var measurementSummary: String? = null,

    @Column(name = "measure_date")
    var measureDate: Instant? = null,

    @Lob
    @Column(name = "measure_note")
    var measureNote: String? = null,

    @Column(name = "measure_address")
    var measureAddress: String? = null,

    @OneToMany(mappedBy = "measurement")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var materials: MutableSet<MaterialMeasurementModel> = mutableSetOf(),

    @ManyToOne
    @JsonIgnoreProperties("measurements")
    var worker: EmployeeModel? = null,

    @ManyToOne
    @JsonIgnoreProperties("measurements")
    var client: CustomerModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addMaterials(materialMeasurement: MaterialMeasurementModel): MeasurementModel {
        this.materials.add(materialMeasurement)
        materialMeasurement.measurement = this
        return this
    }

    fun removeMaterials(materialMeasurement: MaterialMeasurementModel): MeasurementModel {
        this.materials.remove(materialMeasurement)
        materialMeasurement.measurement = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MeasurementModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "MeasurementModel{" +
            "id=$id" +
            ", measurementSummary='$measurementSummary'" +
            ", measureDate='$measureDate'" +
            ", measureNote='$measureNote'" +
            ", measureAddress='$measureAddress'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
