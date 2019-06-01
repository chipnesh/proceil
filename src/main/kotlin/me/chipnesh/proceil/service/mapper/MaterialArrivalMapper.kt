package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.MaterialArrivalModel
import me.chipnesh.proceil.service.dto.MaterialArrivalValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [MaterialArrivalModel] and its DTO [MaterialArrivalValueObject].
 */
@Mapper(componentModel = "spring", uses = [MaterialRequestMapper::class])
interface MaterialArrivalMapper : EntityMapper<MaterialArrivalValueObject, MaterialArrivalModel> {
    @Mappings(
        Mapping(source = "request.id", target = "requestId"),
        Mapping(source = "request.requestSummary", target = "requestRequestSummary")
    )
    override fun toDto(entity: MaterialArrivalModel): MaterialArrivalValueObject
    @Mappings(
        Mapping(source = "requestId", target = "request")
    )
    override fun toEntity(dto: MaterialArrivalValueObject): MaterialArrivalModel

    fun fromId(id: Long?): MaterialArrivalModel? {
        if (id == null) {
            return null
        }
        val materialArrivalModel = MaterialArrivalModel()
        materialArrivalModel.id = id
        return materialArrivalModel
    }
}
