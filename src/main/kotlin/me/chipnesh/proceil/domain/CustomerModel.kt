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
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

import java.io.Serializable
import java.time.Instant
import java.util.Objects

/**
 * Client
 */
@Entity
@Table(name = "customer")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class CustomerModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "customer_summary")
    var customerSummary: String? = null,

    @get: NotNull
    @get: Size(min = 1)
    @Column(name = "firstname", nullable = false)
    var firstname: String? = null,

    @get: NotNull
    @get: Size(min = 1)
    @Column(name = "lastname", nullable = false)
    var lastname: String? = null,

    @Column(name = "middlename")
    var middlename: String? = null,

    @Column(name = "birth_date")
    var birthDate: Instant? = null,

    @get: NotNull
    @get: Size(min = 1)
    @Column(name = "email", nullable = false)
    var email: String? = null,

    @Column(name = "phone")
    var phone: String? = null,

    @Column(name = "address")
    var address: String? = null,

    @OneToMany(mappedBy = "author")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var feedbacks: MutableSet<FeedbackModel> = mutableSetOf(),

    @OneToMany(mappedBy = "client")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var measurements: MutableSet<MeasurementModel> = mutableSetOf(),

    @OneToMany(mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var orders: MutableSet<CustomerOrderModel> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addFeedback(feedback: FeedbackModel): CustomerModel {
        this.feedbacks.add(feedback)
        feedback.author = this
        return this
    }

    fun removeFeedback(feedback: FeedbackModel): CustomerModel {
        this.feedbacks.remove(feedback)
        feedback.author = null
        return this
    }

    fun addMeasurement(measurement: MeasurementModel): CustomerModel {
        this.measurements.add(measurement)
        measurement.client = this
        return this
    }

    fun removeMeasurement(measurement: MeasurementModel): CustomerModel {
        this.measurements.remove(measurement)
        measurement.client = null
        return this
    }

    fun addOrder(customerOrder: CustomerOrderModel): CustomerModel {
        this.orders.add(customerOrder)
        customerOrder.customer = this
        return this
    }

    fun removeOrder(customerOrder: CustomerOrderModel): CustomerModel {
        this.orders.remove(customerOrder)
        customerOrder.customer = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CustomerModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "CustomerModel{" +
            "id=$id" +
            ", customerSummary='$customerSummary'" +
            ", firstname='$firstname'" +
            ", lastname='$lastname'" +
            ", middlename='$middlename'" +
            ", birthDate='$birthDate'" +
            ", email='$email'" +
            ", phone='$phone'" +
            ", address='$address'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
