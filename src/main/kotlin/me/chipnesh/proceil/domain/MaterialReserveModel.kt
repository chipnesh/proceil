package me.chipnesh.proceil.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import java.io.Serializable
import java.time.Instant
import java.util.Objects

import me.chipnesh.proceil.domain.enumeration.MaterialReserveStatus

import me.chipnesh.proceil.domain.enumeration.MeasureUnit
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

/**
 * A MaterialReserveModel.
 */
@Entity
@Table(name = "material_reserve")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@EntityListeners(AuditingEntityListener::class)
class MaterialReserveModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "reserve_date")
    @CreatedDate
    var reserveDate: Instant? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "reserve_status")
    var reserveStatus: MaterialReserveStatus? = null,

    @Column(name = "quantity_to_reserve")
    var quantityToReserve: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "measure_unit")
    var measureUnit: MeasureUnit? = null,

    @ManyToOne
    @JsonIgnoreProperties("materialReserves")
    var material: MaterialModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaterialReserveModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "MaterialReserveModel{" +
            "id=$id" +
            ", reserveDate='$reserveDate'" +
            ", reserveStatus='$reserveStatus'" +
            ", quantityToReserve=$quantityToReserve" +
            ", measureUnit='$measureUnit'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
