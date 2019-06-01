package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.MeasurementModel
import me.chipnesh.proceil.service.dto.MeasurementValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [MeasurementModel] and its DTO [MeasurementValueObject].
 */
@Mapper(componentModel = "spring", uses = [EmployeeMapper::class, CustomerMapper::class])
interface MeasurementMapper : EntityMapper<MeasurementValueObject, MeasurementModel> {
    @Mappings(
        Mapping(source = "worker.id", target = "workerId"),
        Mapping(source = "worker.employeeName", target = "workerEmployeeName"),
        Mapping(source = "client.id", target = "clientId"),
        Mapping(source = "client.customerSummary", target = "clientCustomerSummary")
    )
    override fun toDto(entity: MeasurementModel): MeasurementValueObject
    @Mappings(
        Mapping(target = "materials", ignore = true),
        Mapping(source = "workerId", target = "worker"),
        Mapping(source = "clientId", target = "client")
    )
    override fun toEntity(dto: MeasurementValueObject): MeasurementModel

    fun fromId(id: Long?): MeasurementModel? {
        if (id == null) {
            return null
        }
        val measurementModel = MeasurementModel()
        measurementModel.id = id
        return measurementModel
    }
}
