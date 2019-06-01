package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.ZoneModel
import me.chipnesh.proceil.service.dto.ZoneValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [ZoneModel] and its DTO [ZoneValueObject].
 */
@Mapper(componentModel = "spring", uses = [FacilityMapper::class])
interface ZoneMapper : EntityMapper<ZoneValueObject, ZoneModel> {
    @Mappings(
        Mapping(source = "facility.id", target = "facilityId"),
        Mapping(source = "facility.facilityName", target = "facilityFacilityName")
    )
    override fun toDto(entity: ZoneModel): ZoneValueObject
    @Mappings(
        Mapping(target = "materials", ignore = true),
        Mapping(target = "services", ignore = true),
        Mapping(source = "facilityId", target = "facility")
    )
    override fun toEntity(dto: ZoneValueObject): ZoneModel

    fun fromId(id: Long?): ZoneModel? {
        if (id == null) {
            return null
        }
        val zoneModel = ZoneModel()
        zoneModel.id = id
        return zoneModel
    }
}
