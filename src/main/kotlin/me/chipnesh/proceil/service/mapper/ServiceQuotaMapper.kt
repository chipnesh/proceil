package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.ServiceQuotaModel
import me.chipnesh.proceil.service.dto.ServiceQuotaValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [ServiceQuotaModel] and its DTO [ServiceQuotaValueObject].
 */
@Mapper(componentModel = "spring", uses = [ServiceMapper::class])
interface ServiceQuotaMapper : EntityMapper<ServiceQuotaValueObject, ServiceQuotaModel> {
    @Mappings(
        Mapping(source = "service.id", target = "serviceId"),
        Mapping(source = "service.serviceName", target = "serviceServiceName")
    )
    override fun toDto(entity: ServiceQuotaModel): ServiceQuotaValueObject
    @Mappings(
        Mapping(source = "serviceId", target = "service")
    )
    override fun toEntity(dto: ServiceQuotaValueObject): ServiceQuotaModel

    fun fromId(id: Long?): ServiceQuotaModel? {
        if (id == null) {
            return null
        }
        val serviceQuotaModel = ServiceQuotaModel()
        serviceQuotaModel.id = id
        return serviceQuotaModel
    }
}
