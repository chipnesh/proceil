package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.ServiceAvailabilityModel
import me.chipnesh.proceil.domain.ServiceAvailabilityModel_
import me.chipnesh.proceil.domain.ServiceModel_
import me.chipnesh.proceil.domain.ZoneModel_
import me.chipnesh.proceil.repository.ServiceAvailabilityRepository
import me.chipnesh.proceil.service.dto.ServiceAvailabilityCriteria
import me.chipnesh.proceil.service.dto.ServiceAvailabilityValueObject
import me.chipnesh.proceil.service.mapper.ServiceAvailabilityMapper

/**
 * Service for executing complex queries for [ServiceAvailabilityModel] entities in the database.
 * The main input is a [ServiceAvailabilityCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [ServiceAvailabilityValueObject] or a [Page] of [ServiceAvailabilityValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class ServiceAvailabilityQueryService(
    val serviceAvailabilityRepository: ServiceAvailabilityRepository,
    val serviceAvailabilityMapper: ServiceAvailabilityMapper
) : QueryService<ServiceAvailabilityModel>() {

    private val log = LoggerFactory.getLogger(ServiceAvailabilityQueryService::class.java)

    /**
     * Return a [MutableList] of [ServiceAvailabilityValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ServiceAvailabilityCriteria?): MutableList<ServiceAvailabilityValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return serviceAvailabilityMapper.toDto(serviceAvailabilityRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [ServiceAvailabilityValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ServiceAvailabilityCriteria?, page: Pageable): Page<ServiceAvailabilityValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return serviceAvailabilityRepository.findAll(specification, page)
            .map(serviceAvailabilityMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: ServiceAvailabilityCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return serviceAvailabilityRepository.count(specification)
    }

    /**
     * Function to convert [ServiceAvailabilityCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: ServiceAvailabilityCriteria?): Specification<ServiceAvailabilityModel?> {
        var specification: Specification<ServiceAvailabilityModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, ServiceAvailabilityModel_.id))
            }
            if (criteria.availabilitySummary != null) {
                specification = specification.and(buildStringSpecification(criteria.availabilitySummary, ServiceAvailabilityModel_.availabilitySummary))
            }
            if (criteria.dateFrom != null) {
                specification = specification.and(buildRangeSpecification(criteria.dateFrom, ServiceAvailabilityModel_.dateFrom))
            }
            if (criteria.dateTo != null) {
                specification = specification.and(buildRangeSpecification(criteria.dateTo, ServiceAvailabilityModel_.dateTo))
            }
            if (criteria.remainingQuotas != null) {
                specification = specification.and(buildRangeSpecification(criteria.remainingQuotas, ServiceAvailabilityModel_.remainingQuotas))
            }
            if (criteria.serviceId != null) {
                specification = specification.and(buildSpecification(criteria.serviceId) {
                    it.join(ServiceAvailabilityModel_.service, JoinType.LEFT).get(ServiceModel_.id)
                })
            }
            if (criteria.providedById != null) {
                specification = specification.and(buildSpecification(criteria.providedById) {
                    it.join(ServiceAvailabilityModel_.providedBy, JoinType.LEFT).get(ZoneModel_.id)
                })
            }
        }
        return specification
    }
}
