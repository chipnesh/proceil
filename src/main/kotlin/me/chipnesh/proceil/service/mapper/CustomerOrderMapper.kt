package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.CustomerOrderModel
import me.chipnesh.proceil.service.dto.CustomerOrderValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [CustomerOrderModel] and its DTO [CustomerOrderValueObject].
 */
@Mapper(componentModel = "spring", uses = [EmployeeMapper::class, CustomerMapper::class])
interface CustomerOrderMapper : EntityMapper<CustomerOrderValueObject, CustomerOrderModel> {
    @Mappings(
        Mapping(source = "manager.id", target = "managerId"),
        Mapping(source = "manager.employeeName", target = "managerEmployeeName"),
        Mapping(source = "customer.id", target = "customerId"),
        Mapping(source = "customer.customerSummary", target = "customerCustomerSummary")
    )
    override fun toDto(entity: CustomerOrderModel): CustomerOrderValueObject
    @Mappings(
        Mapping(target = "materials", ignore = true),
        Mapping(target = "services", ignore = true),
        Mapping(source = "managerId", target = "manager"),
        Mapping(source = "customerId", target = "customer")
    )
    override fun toEntity(dto: CustomerOrderValueObject): CustomerOrderModel

    fun fromId(id: Long?): CustomerOrderModel? {
        if (id == null) {
            return null
        }
        val customerOrderModel = CustomerOrderModel()
        customerOrderModel.id = id
        return customerOrderModel
    }
}
