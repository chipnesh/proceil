package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.MaterialAvailabilityModel
import me.chipnesh.proceil.domain.MaterialAvailabilityModel_
import me.chipnesh.proceil.domain.MaterialModel_
import me.chipnesh.proceil.domain.ZoneModel_
import me.chipnesh.proceil.repository.MaterialAvailabilityRepository
import me.chipnesh.proceil.service.dto.MaterialAvailabilityCriteria
import me.chipnesh.proceil.service.dto.MaterialAvailabilityValueObject
import me.chipnesh.proceil.service.mapper.MaterialAvailabilityMapper

/**
 * Service for executing complex queries for [MaterialAvailabilityModel] entities in the database.
 * The main input is a [MaterialAvailabilityCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [MaterialAvailabilityValueObject] or a [Page] of [MaterialAvailabilityValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class MaterialAvailabilityQueryService(
    val materialAvailabilityRepository: MaterialAvailabilityRepository,
    val materialAvailabilityMapper: MaterialAvailabilityMapper
) : QueryService<MaterialAvailabilityModel>() {

    private val log = LoggerFactory.getLogger(MaterialAvailabilityQueryService::class.java)

    /**
     * Return a [MutableList] of [MaterialAvailabilityValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MaterialAvailabilityCriteria?): MutableList<MaterialAvailabilityValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return materialAvailabilityMapper.toDto(materialAvailabilityRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [MaterialAvailabilityValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MaterialAvailabilityCriteria?, page: Pageable): Page<MaterialAvailabilityValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return materialAvailabilityRepository.findAll(specification, page)
            .map(materialAvailabilityMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: MaterialAvailabilityCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return materialAvailabilityRepository.count(specification)
    }

    /**
     * Function to convert [MaterialAvailabilityCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: MaterialAvailabilityCriteria?): Specification<MaterialAvailabilityModel?> {
        var specification: Specification<MaterialAvailabilityModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, MaterialAvailabilityModel_.id))
            }
            if (criteria.availabilitySummary != null) {
                specification = specification.and(buildStringSpecification(criteria.availabilitySummary, MaterialAvailabilityModel_.availabilitySummary))
            }
            if (criteria.remainingQuantity != null) {
                specification = specification.and(buildRangeSpecification(criteria.remainingQuantity, MaterialAvailabilityModel_.remainingQuantity))
            }
            if (criteria.measureUnit != null) {
                specification = specification.and(buildSpecification(criteria.measureUnit, MaterialAvailabilityModel_.measureUnit))
            }
            if (criteria.materialId != null) {
                specification = specification.and(buildSpecification(criteria.materialId) {
                    it.join(MaterialAvailabilityModel_.material, JoinType.LEFT).get(MaterialModel_.id)
                })
            }
            if (criteria.availableAtId != null) {
                specification = specification.and(buildSpecification(criteria.availableAtId) {
                    it.join(MaterialAvailabilityModel_.availableAt, JoinType.LEFT).get(ZoneModel_.id)
                })
            }
        }
        return specification
    }
}
