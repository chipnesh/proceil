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
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table

import java.io.Serializable
import java.time.Instant
import java.util.Objects

import me.chipnesh.proceil.domain.enumeration.OrderStatus

/**
 * A CustomerOrderModel.
 */
@Entity
@Table(name = "customer_order")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class CustomerOrderModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "order_summary")
    var orderSummary: String? = null,

    @Column(name = "created_date")
    var createdDate: Instant? = null,

    @Column(name = "deadline_date")
    var deadlineDate: Instant? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    var orderStatus: OrderStatus? = null,

    @Column(name = "order_paid")
    var orderPaid: Boolean? = null,

    @Lob
    @Column(name = "order_note")
    var orderNote: String? = null,

    @OneToMany(mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var materials: MutableSet<OrderMaterialModel> = mutableSetOf(),

    @OneToMany(mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var services: MutableSet<OrderServiceModel> = mutableSetOf(),

    @ManyToOne
    @JsonIgnoreProperties("customerOrders")
    var manager: EmployeeModel? = null,

    @ManyToOne
    @JsonIgnoreProperties("orders")
    var customer: CustomerModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addMaterials(orderMaterial: OrderMaterialModel): CustomerOrderModel {
        this.materials.add(orderMaterial)
        orderMaterial.order = this
        return this
    }

    fun removeMaterials(orderMaterial: OrderMaterialModel): CustomerOrderModel {
        this.materials.remove(orderMaterial)
        orderMaterial.order = null
        return this
    }

    fun addService(orderService: OrderServiceModel): CustomerOrderModel {
        this.services.add(orderService)
        orderService.order = this
        return this
    }

    fun removeService(orderService: OrderServiceModel): CustomerOrderModel {
        this.services.remove(orderService)
        orderService.order = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CustomerOrderModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "CustomerOrderModel{" +
            "id=$id" +
            ", orderSummary='$orderSummary'" +
            ", createdDate='$createdDate'" +
            ", deadlineDate='$deadlineDate'" +
            ", orderStatus='$orderStatus'" +
            ", orderPaid='$orderPaid'" +
            ", orderNote='$orderNote'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
