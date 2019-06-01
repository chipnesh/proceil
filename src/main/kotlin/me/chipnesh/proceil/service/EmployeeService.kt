package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.EmployeeModel
import me.chipnesh.proceil.repository.EmployeeRepository
import me.chipnesh.proceil.service.dto.EmployeeValueObject
import me.chipnesh.proceil.service.mapper.EmployeeMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [EmployeeModel].
 */
@Service
@Transactional
class EmployeeService(
    val employeeRepository: EmployeeRepository,
    val employeeMapper: EmployeeMapper
) {

    private val log = LoggerFactory.getLogger(EmployeeService::class.java)

    /**
     * Save a employee.
     *
     * @param employeeValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(employeeValueObject: EmployeeValueObject): EmployeeValueObject {
        log.debug("Request to save Employee : {}", employeeValueObject)

        var employeeModel = employeeMapper.toEntity(employeeValueObject)
        employeeModel = employeeRepository.save(employeeModel)
        return employeeMapper.toDto(employeeModel)
    }

    /**
     * Get all the employees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<EmployeeValueObject> {
        log.debug("Request to get all Employees")
        return employeeRepository.findAll(pageable)
            .map(employeeMapper::toDto)
    }

    /**
     * Get one employee by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<EmployeeValueObject> {
        log.debug("Request to get Employee : {}", id)
        return employeeRepository.findById(id)
            .map(employeeMapper::toDto)
    }

    /**
     * Delete the employee by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Employee : {}", id)

        employeeRepository.deleteById(id)
    }
}
