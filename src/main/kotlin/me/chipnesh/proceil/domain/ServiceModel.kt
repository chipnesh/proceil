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
import java.math.BigDecimal
import java.util.Objects

/**
 * A ServiceModel.
 */
@Entity
@Table(name = "service")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class ServiceModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "service_name")
    var serviceName: String? = null,

    @Column(name = "service_description")
    var serviceDescription: String? = null,

    @Column(name = "service_price", precision = 21, scale = 2)
    var servicePrice: BigDecimal? = null,

    @OneToMany(mappedBy = "service")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var images: MutableSet<AttachedImageModel> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addImage(attachedImage: AttachedImageModel): ServiceModel {
        this.images.add(attachedImage)
        attachedImage.service = this
        return this
    }

    fun removeImage(attachedImage: AttachedImageModel): ServiceModel {
        this.images.remove(attachedImage)
        attachedImage.service = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ServiceModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "ServiceModel{" +
            "id=$id" +
            ", serviceName='$serviceName'" +
            ", serviceDescription='$serviceDescription'" +
            ", servicePrice=$servicePrice" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
