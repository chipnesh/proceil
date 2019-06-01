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

import java.io.Serializable
import java.util.Objects

/**
 * A FacilityModel.
 */
@Entity
@Table(name = "facility")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class FacilityModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "facility_name")
    var facilityName: String? = null,

    @OneToMany(mappedBy = "facility")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var zones: MutableSet<ZoneModel> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addZone(zone: ZoneModel): FacilityModel {
        this.zones.add(zone)
        zone.facility = this
        return this
    }

    fun removeZone(zone: ZoneModel): FacilityModel {
        this.zones.remove(zone)
        zone.facility = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FacilityModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "FacilityModel{" +
            "id=$id" +
            ", facilityName='$facilityName'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
