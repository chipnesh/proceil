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
 * A MaterialModel.
 */
@Entity
@Table(name = "material")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class MaterialModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "material_name")
    var materialName: String? = null,

    @Column(name = "material_description")
    var materialDescription: String? = null,

    @Column(name = "material_price", precision = 21, scale = 2)
    var materialPrice: BigDecimal? = null,

    @OneToMany(mappedBy = "material")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var images: MutableSet<AttachedImageModel> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addImage(attachedImage: AttachedImageModel): MaterialModel {
        this.images.add(attachedImage)
        attachedImage.material = this
        return this
    }

    fun removeImage(attachedImage: AttachedImageModel): MaterialModel {
        this.images.remove(attachedImage)
        attachedImage.material = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaterialModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "MaterialModel{" +
            "id=$id" +
            ", materialName='$materialName'" +
            ", materialDescription='$materialDescription'" +
            ", materialPrice=$materialPrice" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
