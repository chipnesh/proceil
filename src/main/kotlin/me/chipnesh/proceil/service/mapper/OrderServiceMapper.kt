package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.OrderServiceModel
import me.chipnesh.proceil.service.dto.OrderServiceValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [OrderServiceModel] and its DTO [OrderServiceValueObject].
 */
@Mapper(componentModel = "spring", uses = [ServiceQuotaMapper::class, EmployeeMapper::class, CustomerOrderMapper::class])
interface OrderServiceMapper : EntityMapper<OrderServiceValueObject, OrderServiceModel> {
    @Mappings(
        Mapping(source = "quota.id", target = "quotaId"),
        Mapping(source = "quota.quotaStatus", target = "quotaQuotaStatus"),
        Mapping(source = "executor.id", target = "executorId"),
        Mapping(source = "executor.employeeName", target = "executorEmployeeName"),
        Mapping(source = "order.id", target = "orderId"),
        Mapping(source = "order.orderSummary", target = "orderOrderSummary")
    )
    override fun toDto(entity: OrderServiceModel): OrderServiceValueObject
    @Mappings(
        Mapping(source = "quotaId", target = "quota"),
        Mapping(source = "executorId", target = "executor"),
        Mapping(source = "orderId", target = "order")
    )
    override fun toEntity(dto: OrderServiceValueObject): OrderServiceModel

    fun fromId(id: Long?): OrderServiceModel? {
        if (id == null) {
            return null
        }
        val orderServiceModel = OrderServiceModel()
        orderServiceModel.id = id
        return orderServiceModel
    }
}
