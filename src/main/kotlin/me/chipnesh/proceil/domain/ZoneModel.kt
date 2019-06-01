package me.chipnesh.proceil.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table

import java.io.Serializable
import java.util.Objects

/**
 * A ZoneModel.
 */
@Entity
@Table(name = "zone")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class ZoneModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "zone_name")
    var zoneName: String? = null,

    @OneToMany(mappedBy = "availableAt")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var materials: MutableSet<MaterialAvailabilityModel> = mutableSetOf(),

    @OneToMany(mappedBy = "providedBy")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var services: MutableSet<ServiceAvailabilityModel> = mutableSetOf(),

    @ManyToOne
    @JsonIgnoreProperties("zones")
    var facility: FacilityModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addMaterial(materialAvailability: MaterialAvailabilityModel): ZoneModel {
        this.materials.add(materialAvailability)
        materialAvailability.availableAt = this
        return this
    }

    fun removeMaterial(materialAvailability: MaterialAvailabilityModel): ZoneModel {
        this.materials.remove(materialAvailability)
        materialAvailability.availableAt = null
        return this
    }

    fun addService(serviceAvailability: ServiceAvailabilityModel): ZoneModel {
        this.services.add(serviceAvailability)
        serviceAvailability.providedBy = this
        return this
    }

    fun removeService(serviceAvailability: ServiceAvailabilityModel): ZoneModel {
        this.services.remove(serviceAvailability)
        serviceAvailability.providedBy = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ZoneModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "ZoneModel{" +
            "id=$id" +
            ", zoneName='$zoneName'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
