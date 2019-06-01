package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.ZoneModel
import me.chipnesh.proceil.domain.ZoneModel_
import me.chipnesh.proceil.domain.FacilityModel_
import me.chipnesh.proceil.domain.MaterialAvailabilityModel_
import me.chipnesh.proceil.domain.ServiceAvailabilityModel_
import me.chipnesh.proceil.repository.ZoneRepository
import me.chipnesh.proceil.service.dto.ZoneCriteria
import me.chipnesh.proceil.service.dto.ZoneValueObject
import me.chipnesh.proceil.service.mapper.ZoneMapper

/**
 * Service for executing complex queries for [ZoneModel] entities in the database.
 * The main input is a [ZoneCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [ZoneValueObject] or a [Page] of [ZoneValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class ZoneQueryService(
    val zoneRepository: ZoneRepository,
    val zoneMapper: ZoneMapper
) : QueryService<ZoneModel>() {

    private val log = LoggerFactory.getLogger(ZoneQueryService::class.java)

    /**
     * Return a [MutableList] of [ZoneValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ZoneCriteria?): MutableList<ZoneValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return zoneMapper.toDto(zoneRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [ZoneValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ZoneCriteria?, page: Pageable): Page<ZoneValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return zoneRepository.findAll(specification, page)
            .map(zoneMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: ZoneCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return zoneRepository.count(specification)
    }

    /**
     * Function to convert [ZoneCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: ZoneCriteria?): Specification<ZoneModel?> {
        var specification: Specification<ZoneModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, ZoneModel_.id))
            }
            if (criteria.zoneName != null) {
                specification = specification.and(buildStringSpecification(criteria.zoneName, ZoneModel_.zoneName))
            }
            if (criteria.materialId != null) {
                specification = specification.and(buildSpecification(criteria.materialId) {
                    it.join(ZoneModel_.materials, JoinType.LEFT).get(MaterialAvailabilityModel_.id)
                })
            }
            if (criteria.serviceId != null) {
                specification = specification.and(buildSpecification(criteria.serviceId) {
                    it.join(ZoneModel_.services, JoinType.LEFT).get(ServiceAvailabilityModel_.id)
                })
            }
            if (criteria.facilityId != null) {
                specification = specification.and(buildSpecification(criteria.facilityId) {
                    it.join(ZoneModel_.facility, JoinType.LEFT).get(FacilityModel_.id)
                })
            }
        }
        return specification
    }
}
