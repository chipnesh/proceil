package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.MaterialReserveModel
import me.chipnesh.proceil.service.dto.MaterialReserveValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [MaterialReserveModel] and its DTO [MaterialReserveValueObject].
 */
@Mapper(componentModel = "spring", uses = [MaterialMapper::class])
interface MaterialReserveMapper : EntityMapper<MaterialReserveValueObject, MaterialReserveModel> {
    @Mappings(
        Mapping(source = "material.id", target = "materialId"),
        Mapping(source = "material.materialName", target = "materialMaterialName")
    )
    override fun toDto(entity: MaterialReserveModel): MaterialReserveValueObject
    @Mappings(
        Mapping(source = "materialId", target = "material")
    )
    override fun toEntity(dto: MaterialReserveValueObject): MaterialReserveModel

    fun fromId(id: Long?): MaterialReserveModel? {
        if (id == null) {
            return null
        }
        val materialReserveModel = MaterialReserveModel()
        materialReserveModel.id = id
        return materialReserveModel
    }
}
