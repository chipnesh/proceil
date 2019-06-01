package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.AttachedImageModel
import me.chipnesh.proceil.service.dto.AttachedImageValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [AttachedImageModel] and its DTO [AttachedImageValueObject].
 */
@Mapper(componentModel = "spring", uses = [MaterialMapper::class, ServiceMapper::class])
interface AttachedImageMapper : EntityMapper<AttachedImageValueObject, AttachedImageModel> {
    @Mappings(
        Mapping(source = "material.id", target = "materialId"),
        Mapping(source = "service.id", target = "serviceId")
    )
    override fun toDto(entity: AttachedImageModel): AttachedImageValueObject
    @Mappings(
        Mapping(source = "materialId", target = "material"),
        Mapping(source = "serviceId", target = "service")
    )
    override fun toEntity(dto: AttachedImageValueObject): AttachedImageModel

    fun fromId(id: Long?): AttachedImageModel? {
        if (id == null) {
            return null
        }
        val attachedImageModel = AttachedImageModel()
        attachedImageModel.id = id
        return attachedImageModel
    }
}
