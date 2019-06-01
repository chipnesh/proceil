package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.OrderMaterialModel
import me.chipnesh.proceil.service.dto.OrderMaterialValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [OrderMaterialModel] and its DTO [OrderMaterialValueObject].
 */
@Mapper(componentModel = "spring", uses = [MaterialReserveMapper::class, CustomerOrderMapper::class])
interface OrderMaterialMapper : EntityMapper<OrderMaterialValueObject, OrderMaterialModel> {
    @Mappings(
        Mapping(source = "reserve.id", target = "reserveId"),
        Mapping(source = "reserve.reserveStatus", target = "reserveReserveStatus"),
        Mapping(source = "order.id", target = "orderId"),
        Mapping(source = "order.orderSummary", target = "orderOrderSummary")
    )
    override fun toDto(entity: OrderMaterialModel): OrderMaterialValueObject
    @Mappings(
        Mapping(source = "reserveId", target = "reserve"),
        Mapping(source = "orderId", target = "order")
    )
    override fun toEntity(dto: OrderMaterialValueObject): OrderMaterialModel

    fun fromId(id: Long?): OrderMaterialModel? {
        if (id == null) {
            return null
        }
        val orderMaterialModel = OrderMaterialModel()
        orderMaterialModel.id = id
        return orderMaterialModel
    }
}
