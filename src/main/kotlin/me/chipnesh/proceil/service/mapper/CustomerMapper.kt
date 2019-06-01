package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.CustomerModel
import me.chipnesh.proceil.service.dto.CustomerValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [CustomerModel] and its DTO [CustomerValueObject].
 */
@Mapper(componentModel = "spring", uses = [])
interface CustomerMapper : EntityMapper<CustomerValueObject, CustomerModel> {
    @Mappings(
        Mapping(target = "feedbacks", ignore = true),
        Mapping(target = "measurements", ignore = true),
        Mapping(target = "orders", ignore = true)
    )
    override fun toEntity(dto: CustomerValueObject): CustomerModel

    fun fromId(id: Long?): CustomerModel? {
        if (id == null) {
            return null
        }
        val customerModel = CustomerModel()
        customerModel.id = id
        return customerModel
    }
}
