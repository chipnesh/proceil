package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.FacilityModel
import me.chipnesh.proceil.domain.FacilityModel_
import me.chipnesh.proceil.domain.ZoneModel_
import me.chipnesh.proceil.repository.FacilityRepository
import me.chipnesh.proceil.service.dto.FacilityCriteria
import me.chipnesh.proceil.service.dto.FacilityValueObject
import me.chipnesh.proceil.service.mapper.FacilityMapper

/**
 * Service for executing complex queries for [FacilityModel] entities in the database.
 * The main input is a [FacilityCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [FacilityValueObject] or a [Page] of [FacilityValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class FacilityQueryService(
    val facilityRepository: FacilityRepository,
    val facilityMapper: FacilityMapper
) : QueryService<FacilityModel>() {

    private val log = LoggerFactory.getLogger(FacilityQueryService::class.java)

    /**
     * Return a [MutableList] of [FacilityValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: FacilityCriteria?): MutableList<FacilityValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return facilityMapper.toDto(facilityRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [FacilityValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: FacilityCriteria?, page: Pageable): Page<FacilityValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return facilityRepository.findAll(specification, page)
            .map(facilityMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: FacilityCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return facilityRepository.count(specification)
    }

    /**
     * Function to convert [FacilityCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: FacilityCriteria?): Specification<FacilityModel?> {
        var specification: Specification<FacilityModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, FacilityModel_.id))
            }
            if (criteria.facilityName != null) {
                specification = specification.and(buildStringSpecification(criteria.facilityName, FacilityModel_.facilityName))
            }
            if (criteria.zoneId != null) {
                specification = specification.and(buildSpecification(criteria.zoneId) {
                    it.join(FacilityModel_.zones, JoinType.LEFT).get(ZoneModel_.id)
                })
            }
        }
        return specification
    }
}
