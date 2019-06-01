package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.MaterialRequestModel
import me.chipnesh.proceil.service.dto.MaterialRequestValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [MaterialRequestModel] and its DTO [MaterialRequestValueObject].
 */
@Mapper(componentModel = "spring", uses = [FacilityMapper::class, MaterialMapper::class])
interface MaterialRequestMapper : EntityMapper<MaterialRequestValueObject, MaterialRequestModel> {
    @Mappings(
        Mapping(source = "requester.id", target = "requesterId"),
        Mapping(source = "requester.facilityName", target = "requesterFacilityName"),
        Mapping(source = "material.id", target = "materialId"),
        Mapping(source = "material.materialName", target = "materialMaterialName")
    )
    override fun toDto(entity: MaterialRequestModel): MaterialRequestValueObject
    @Mappings(
        Mapping(source = "requesterId", target = "requester"),
        Mapping(source = "materialId", target = "material")
    )
    override fun toEntity(dto: MaterialRequestValueObject): MaterialRequestModel

    fun fromId(id: Long?): MaterialRequestModel? {
        if (id == null) {
            return null
        }
        val materialRequestModel = MaterialRequestModel()
        materialRequestModel.id = id
        return materialRequestModel
    }
}
