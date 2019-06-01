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
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

import java.io.Serializable
import java.time.Instant
import java.util.Objects

import me.chipnesh.proceil.domain.enumeration.MeasureUnit

/**
 * A OrderMaterialModel.
 */
@Entity
@Table(name = "order_material")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class OrderMaterialModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "material_summary")
    var materialSummary: String? = null,

    @Column(name = "created_date")
    var createdDate: Instant? = null,

    @get: NotNull
    @get: Min(value = 0)
    @Column(name = "material_quantity", nullable = false)
    var materialQuantity: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "measure_unit")
    var measureUnit: MeasureUnit? = null,

    @OneToOne
    @JoinColumn(unique = true)
    var reserve: MaterialReserveModel? = null,

    @ManyToOne
    @JsonIgnoreProperties("materials")
    var order: CustomerOrderModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderMaterialModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "OrderMaterialModel{" +
            "id=$id" +
            ", materialSummary='$materialSummary'" +
            ", createdDate='$createdDate'" +
            ", materialQuantity=$materialQuantity" +
            ", measureUnit='$measureUnit'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
