package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.EmployeeModel
import me.chipnesh.proceil.service.dto.EmployeeValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [EmployeeModel] and its DTO [EmployeeValueObject].
 */
@Mapper(componentModel = "spring", uses = [])
interface EmployeeMapper : EntityMapper<EmployeeValueObject, EmployeeModel> {
    @Mappings(
        Mapping(target = "measurements", ignore = true)
    )
    override fun toEntity(dto: EmployeeValueObject): EmployeeModel

    fun fromId(id: Long?): EmployeeModel? {
        if (id == null) {
            return null
        }
        val employeeModel = EmployeeModel()
        employeeModel.id = id
        return employeeModel
    }
}
