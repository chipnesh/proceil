package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.FacilityModel
import me.chipnesh.proceil.service.dto.FacilityValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [FacilityModel] and its DTO [FacilityValueObject].
 */
@Mapper(componentModel = "spring", uses = [])
interface FacilityMapper : EntityMapper<FacilityValueObject, FacilityModel> {
    @Mappings(
        Mapping(target = "zones", ignore = true)
    )
    override fun toEntity(dto: FacilityValueObject): FacilityModel

    fun fromId(id: Long?): FacilityModel? {
        if (id == null) {
            return null
        }
        val facilityModel = FacilityModel()
        facilityModel.id = id
        return facilityModel
    }
}
