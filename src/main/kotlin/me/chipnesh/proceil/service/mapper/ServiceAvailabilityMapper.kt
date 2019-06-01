package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.ServiceAvailabilityModel
import me.chipnesh.proceil.service.dto.ServiceAvailabilityValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [ServiceAvailabilityModel] and its DTO [ServiceAvailabilityValueObject].
 */
@Mapper(componentModel = "spring", uses = [ServiceMapper::class, ZoneMapper::class])
interface ServiceAvailabilityMapper : EntityMapper<ServiceAvailabilityValueObject, ServiceAvailabilityModel> {
    @Mappings(
        Mapping(source = "service.id", target = "serviceId"),
        Mapping(source = "service.serviceName", target = "serviceServiceName"),
        Mapping(source = "providedBy.id", target = "providedById"),
        Mapping(source = "providedBy.zoneName", target = "providedByZoneName")
    )
    override fun toDto(entity: ServiceAvailabilityModel): ServiceAvailabilityValueObject
    @Mappings(
        Mapping(source = "serviceId", target = "service"),
        Mapping(source = "providedById", target = "providedBy")
    )
    override fun toEntity(dto: ServiceAvailabilityValueObject): ServiceAvailabilityModel

    fun fromId(id: Long?): ServiceAvailabilityModel? {
        if (id == null) {
            return null
        }
        val serviceAvailabilityModel = ServiceAvailabilityModel()
        serviceAvailabilityModel.id = id
        return serviceAvailabilityModel
    }
}
