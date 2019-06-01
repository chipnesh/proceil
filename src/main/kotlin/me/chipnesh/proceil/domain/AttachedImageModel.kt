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
import javax.persistence.SequenceGenerator
import javax.persistence.Table

import java.io.Serializable
import java.util.Objects

/**
 * A AttachedImageModel.
 */
@Entity
@Table(name = "attached_image")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class AttachedImageModel(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "image_name")
    var imageName: String? = null,

    @Lob
    @Column(name = "image_file")
    var imageFile: ByteArray? = null,

    @Column(name = "image_file_content_type")
    var imageFileContentType: String? = null,

    @ManyToOne
    @JsonIgnoreProperties("attachedImages")
    var material: MaterialModel? = null,

    @ManyToOne
    @JsonIgnoreProperties("attachedImages")
    var service: ServiceModel? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AttachedImageModel) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "AttachedImageModel{" +
            "id=$id" +
            ", imageName='$imageName'" +
            ", imageFile='$imageFile'" +
            ", imageFileContentType='$imageFileContentType'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
