package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.EmployeeModel
import me.chipnesh.proceil.domain.EmployeeModel_
import me.chipnesh.proceil.domain.MeasurementModel_
import me.chipnesh.proceil.repository.EmployeeRepository
import me.chipnesh.proceil.service.dto.EmployeeCriteria
import me.chipnesh.proceil.service.dto.EmployeeValueObject
import me.chipnesh.proceil.service.mapper.EmployeeMapper

/**
 * Service for executing complex queries for [EmployeeModel] entities in the database.
 * The main input is a [EmployeeCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [EmployeeValueObject] or a [Page] of [EmployeeValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class EmployeeQueryService(
    val employeeRepository: EmployeeRepository,
    val employeeMapper: EmployeeMapper
) : QueryService<EmployeeModel>() {

    private val log = LoggerFactory.getLogger(EmployeeQueryService::class.java)

    /**
     * Return a [MutableList] of [EmployeeValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: EmployeeCriteria?): MutableList<EmployeeValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return employeeMapper.toDto(employeeRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [EmployeeValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: EmployeeCriteria?, page: Pageable): Page<EmployeeValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return employeeRepository.findAll(specification, page)
            .map(employeeMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: EmployeeCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return employeeRepository.count(specification)
    }

    /**
     * Function to convert [EmployeeCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: EmployeeCriteria?): Specification<EmployeeModel?> {
        var specification: Specification<EmployeeModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, EmployeeModel_.id))
            }
            if (criteria.employeeName != null) {
                specification = specification.and(buildStringSpecification(criteria.employeeName, EmployeeModel_.employeeName))
            }
            if (criteria.phone != null) {
                specification = specification.and(buildStringSpecification(criteria.phone, EmployeeModel_.phone))
            }
            if (criteria.measurementId != null) {
                specification = specification.and(buildSpecification(criteria.measurementId) {
                    it.join(EmployeeModel_.measurements, JoinType.LEFT).get(MeasurementModel_.id)
                })
            }
        }
        return specification
    }
}
