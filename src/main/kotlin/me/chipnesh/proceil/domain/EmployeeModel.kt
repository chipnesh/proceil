package me.chipnesh.proceil.domain

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.Size

import java.io.Serializable
import java.util.Objects

/**
 * A EmployeeModel.
 */
@Entity
@Table(name = "employee")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class EmployeeModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @get: Size(min = 1)
    @Column(name = "employee_name")
    var employeeName: String? = null,

    @get: Size(min = 1)
    @Column(name = "phone")
    var phone: String? = null,

    @OneToMany(mappedBy = "worker")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var measurements: MutableSet<MeasurementModel> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addMeasurement(measurement: MeasurementModel): EmployeeModel {
        this.measurements.add(measurement)
        measurement.worker = this
        return this
    }

    fun removeMeasurement(measurement: MeasurementModel): EmployeeModel {
        this.measurements.remove(measurement)
        measurement.worker = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmployeeModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "EmployeeModel{" +
            "id=$id" +
            ", employeeName='$employeeName'" +
            ", phone='$phone'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
