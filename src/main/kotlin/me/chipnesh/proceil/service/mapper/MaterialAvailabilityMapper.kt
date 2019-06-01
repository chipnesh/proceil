package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.MaterialAvailabilityModel
import me.chipnesh.proceil.service.dto.MaterialAvailabilityValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [MaterialAvailabilityModel] and its DTO [MaterialAvailabilityValueObject].
 */
@Mapper(componentModel = "spring", uses = [MaterialMapper::class, ZoneMapper::class])
interface MaterialAvailabilityMapper : EntityMapper<MaterialAvailabilityValueObject, MaterialAvailabilityModel> {
    @Mappings(
        Mapping(source = "material.id", target = "materialId"),
        Mapping(source = "material.materialName", target = "materialMaterialName"),
        Mapping(source = "availableAt.id", target = "availableAtId"),
        Mapping(source = "availableAt.zoneName", target = "availableAtZoneName")
    )
    override fun toDto(entity: MaterialAvailabilityModel): MaterialAvailabilityValueObject
    @Mappings(
        Mapping(source = "materialId", target = "material"),
        Mapping(source = "availableAtId", target = "availableAt")
    )
    override fun toEntity(dto: MaterialAvailabilityValueObject): MaterialAvailabilityModel

    fun fromId(id: Long?): MaterialAvailabilityModel? {
        if (id == null) {
            return null
        }
        val materialAvailabilityModel = MaterialAvailabilityModel()
        materialAvailabilityModel.id = id
        return materialAvailabilityModel
    }
}
