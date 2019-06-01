package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.MaterialMeasurementModel
import me.chipnesh.proceil.domain.MaterialMeasurementModel_
import me.chipnesh.proceil.domain.MaterialModel_
import me.chipnesh.proceil.domain.MeasurementModel_
import me.chipnesh.proceil.repository.MaterialMeasurementRepository
import me.chipnesh.proceil.service.dto.MaterialMeasurementCriteria
import me.chipnesh.proceil.service.dto.MaterialMeasurementValueObject
import me.chipnesh.proceil.service.mapper.MaterialMeasurementMapper

/**
 * Service for executing complex queries for [MaterialMeasurementModel] entities in the database.
 * The main input is a [MaterialMeasurementCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [MaterialMeasurementValueObject] or a [Page] of [MaterialMeasurementValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class MaterialMeasurementQueryService(
    val materialMeasurementRepository: MaterialMeasurementRepository,
    val materialMeasurementMapper: MaterialMeasurementMapper
) : QueryService<MaterialMeasurementModel>() {

    private val log = LoggerFactory.getLogger(MaterialMeasurementQueryService::class.java)

    /**
     * Return a [MutableList] of [MaterialMeasurementValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MaterialMeasurementCriteria?): MutableList<MaterialMeasurementValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return materialMeasurementMapper.toDto(materialMeasurementRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [MaterialMeasurementValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MaterialMeasurementCriteria?, page: Pageable): Page<MaterialMeasurementValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return materialMeasurementRepository.findAll(specification, page)
            .map(materialMeasurementMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: MaterialMeasurementCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return materialMeasurementRepository.count(specification)
    }

    /**
     * Function to convert [MaterialMeasurementCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: MaterialMeasurementCriteria?): Specification<MaterialMeasurementModel?> {
        var specification: Specification<MaterialMeasurementModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, MaterialMeasurementModel_.id))
            }
            if (criteria.measurementSummary != null) {
                specification = specification.and(buildStringSpecification(criteria.measurementSummary, MaterialMeasurementModel_.measurementSummary))
            }
            if (criteria.measurementValue != null) {
                specification = specification.and(buildRangeSpecification(criteria.measurementValue, MaterialMeasurementModel_.measurementValue))
            }
            if (criteria.measureUnit != null) {
                specification = specification.and(buildSpecification(criteria.measureUnit, MaterialMeasurementModel_.measureUnit))
            }
            if (criteria.materialId != null) {
                specification = specification.and(buildSpecification(criteria.materialId) {
                    it.join(MaterialMeasurementModel_.material, JoinType.LEFT).get(MaterialModel_.id)
                })
            }
            if (criteria.measurementId != null) {
                specification = specification.and(buildSpecification(criteria.measurementId) {
                    it.join(MaterialMeasurementModel_.measurement, JoinType.LEFT).get(MeasurementModel_.id)
                })
            }
        }
        return specification
    }
}
