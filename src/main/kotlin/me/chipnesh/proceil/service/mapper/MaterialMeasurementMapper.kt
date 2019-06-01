package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.MaterialMeasurementModel
import me.chipnesh.proceil.service.dto.MaterialMeasurementValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [MaterialMeasurementModel] and its DTO [MaterialMeasurementValueObject].
 */
@Mapper(componentModel = "spring", uses = [MaterialMapper::class, MeasurementMapper::class])
interface MaterialMeasurementMapper : EntityMapper<MaterialMeasurementValueObject, MaterialMeasurementModel> {
    @Mappings(
        Mapping(source = "material.id", target = "materialId"),
        Mapping(source = "material.materialName", target = "materialMaterialName"),
        Mapping(source = "measurement.id", target = "measurementId"),
        Mapping(source = "measurement.measurementSummary", target = "measurementMeasurementSummary")
    )
    override fun toDto(entity: MaterialMeasurementModel): MaterialMeasurementValueObject
    @Mappings(
        Mapping(source = "materialId", target = "material"),
        Mapping(source = "measurementId", target = "measurement")
    )
    override fun toEntity(dto: MaterialMeasurementValueObject): MaterialMeasurementModel

    fun fromId(id: Long?): MaterialMeasurementModel? {
        if (id == null) {
            return null
        }
        val materialMeasurementModel = MaterialMeasurementModel()
        materialMeasurementModel.id = id
        return materialMeasurementModel
    }
}
