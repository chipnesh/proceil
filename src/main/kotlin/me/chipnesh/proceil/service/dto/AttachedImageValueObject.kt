package me.chipnesh.proceil.service.dto

import java.io.Serializable
import java.util.Objects
import javax.persistence.Lob

/**
 * A DTO for the [me.chipnesh.proceil.domain.AttachedImageModel] entity.
 */
data class AttachedImageValueObject(

    var id: Long? = null,

    var imageName: String? = null,

    @Lob
    var imageFile: ByteArray? = null,
    var imageFileContentType: String? = null,

    var materialId: Long? = null,

    var serviceId: Long? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AttachedImageValueObject) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
