package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.ServiceModel
import me.chipnesh.proceil.domain.ServiceModel_
import me.chipnesh.proceil.domain.AttachedImageModel_
import me.chipnesh.proceil.repository.ServiceRepository
import me.chipnesh.proceil.service.dto.ServiceCriteria
import me.chipnesh.proceil.service.dto.ServiceValueObject
import me.chipnesh.proceil.service.mapper.ServiceMapper

/**
 * Service for executing complex queries for [ServiceModel] entities in the database.
 * The main input is a [ServiceCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [ServiceValueObject] or a [Page] of [ServiceValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class ServiceQueryService(
    val serviceRepository: ServiceRepository,
    val serviceMapper: ServiceMapper
) : QueryService<ServiceModel>() {

    private val log = LoggerFactory.getLogger(ServiceQueryService::class.java)

    /**
     * Return a [MutableList] of [ServiceValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ServiceCriteria?): MutableList<ServiceValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return serviceMapper.toDto(serviceRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [ServiceValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ServiceCriteria?, page: Pageable): Page<ServiceValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return serviceRepository.findAll(specification, page)
            .map(serviceMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: ServiceCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return serviceRepository.count(specification)
    }

    /**
     * Function to convert [ServiceCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: ServiceCriteria?): Specification<ServiceModel?> {
        var specification: Specification<ServiceModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, ServiceModel_.id))
            }
            if (criteria.serviceName != null) {
                specification = specification.and(buildStringSpecification(criteria.serviceName, ServiceModel_.serviceName))
            }
            if (criteria.serviceDescription != null) {
                specification = specification.and(buildStringSpecification(criteria.serviceDescription, ServiceModel_.serviceDescription))
            }
            if (criteria.servicePrice != null) {
                specification = specification.and(buildRangeSpecification(criteria.servicePrice, ServiceModel_.servicePrice))
            }
            if (criteria.imageId != null) {
                specification = specification.and(buildSpecification(criteria.imageId) {
                    it.join(ServiceModel_.images, JoinType.LEFT).get(AttachedImageModel_.id)
                })
            }
        }
        return specification
    }
}
