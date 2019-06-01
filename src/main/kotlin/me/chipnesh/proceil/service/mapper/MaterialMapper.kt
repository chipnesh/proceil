package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.MaterialModel
import me.chipnesh.proceil.service.dto.MaterialValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [MaterialModel] and its DTO [MaterialValueObject].
 */
@Mapper(componentModel = "spring", uses = [])
interface MaterialMapper : EntityMapper<MaterialValueObject, MaterialModel> {
    @Mappings(
        Mapping(target = "images", ignore = true)
    )
    override fun toEntity(dto: MaterialValueObject): MaterialModel

    fun fromId(id: Long?): MaterialModel? {
        if (id == null) {
            return null
        }
        val materialModel = MaterialModel()
        materialModel.id = id
        return materialModel
    }
}
