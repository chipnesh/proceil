package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.ServiceModel
import me.chipnesh.proceil.service.dto.ServiceValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [ServiceModel] and its DTO [ServiceValueObject].
 */
@Mapper(componentModel = "spring", uses = [])
interface ServiceMapper : EntityMapper<ServiceValueObject, ServiceModel> {
    @Mappings(
        Mapping(target = "images", ignore = true)
    )
    override fun toEntity(dto: ServiceValueObject): ServiceModel

    fun fromId(id: Long?): ServiceModel? {
        if (id == null) {
            return null
        }
        val serviceModel = ServiceModel()
        serviceModel.id = id
        return serviceModel
    }
}
