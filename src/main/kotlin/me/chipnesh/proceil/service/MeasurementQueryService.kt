package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.MeasurementModel
import me.chipnesh.proceil.domain.MeasurementModel_
import me.chipnesh.proceil.domain.CustomerModel_
import me.chipnesh.proceil.domain.EmployeeModel_
import me.chipnesh.proceil.domain.MaterialMeasurementModel_
import me.chipnesh.proceil.repository.MeasurementRepository
import me.chipnesh.proceil.service.dto.MeasurementCriteria
import me.chipnesh.proceil.service.dto.MeasurementValueObject
import me.chipnesh.proceil.service.mapper.MeasurementMapper

/**
 * Service for executing complex queries for [MeasurementModel] entities in the database.
 * The main input is a [MeasurementCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [MeasurementValueObject] or a [Page] of [MeasurementValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class MeasurementQueryService(
    val measurementRepository: MeasurementRepository,
    val measurementMapper: MeasurementMapper
) : QueryService<MeasurementModel>() {

    private val log = LoggerFactory.getLogger(MeasurementQueryService::class.java)

    /**
     * Return a [MutableList] of [MeasurementValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MeasurementCriteria?): MutableList<MeasurementValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return measurementMapper.toDto(measurementRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [MeasurementValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MeasurementCriteria?, page: Pageable): Page<MeasurementValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return measurementRepository.findAll(specification, page)
            .map(measurementMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: MeasurementCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return measurementRepository.count(specification)
    }

    /**
     * Function to convert [MeasurementCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: MeasurementCriteria?): Specification<MeasurementModel?> {
        var specification: Specification<MeasurementModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, MeasurementModel_.id))
            }
            if (criteria.measurementSummary != null) {
                specification = specification.and(buildStringSpecification(criteria.measurementSummary, MeasurementModel_.measurementSummary))
            }
            if (criteria.measureDate != null) {
                specification = specification.and(buildRangeSpecification(criteria.measureDate, MeasurementModel_.measureDate))
            }
            if (criteria.measureAddress != null) {
                specification = specification.and(buildStringSpecification(criteria.measureAddress, MeasurementModel_.measureAddress))
            }
            if (criteria.materialsId != null) {
                specification = specification.and(buildSpecification(criteria.materialsId) {
                    it.join(MeasurementModel_.materials, JoinType.LEFT).get(MaterialMeasurementModel_.id)
                })
            }
            if (criteria.workerId != null) {
                specification = specification.and(buildSpecification(criteria.workerId) {
                    it.join(MeasurementModel_.worker, JoinType.LEFT).get(EmployeeModel_.id)
                })
            }
            if (criteria.clientId != null) {
                specification = specification.and(buildSpecification(criteria.clientId) {
                    it.join(MeasurementModel_.client, JoinType.LEFT).get(CustomerModel_.id)
                })
            }
        }
        return specification
    }
}
