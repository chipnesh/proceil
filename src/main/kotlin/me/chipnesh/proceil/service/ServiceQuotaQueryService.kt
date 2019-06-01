package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.ServiceQuotaModel
import me.chipnesh.proceil.domain.ServiceQuotaModel_
import me.chipnesh.proceil.domain.ServiceModel_
import me.chipnesh.proceil.repository.ServiceQuotaRepository
import me.chipnesh.proceil.service.dto.ServiceQuotaCriteria
import me.chipnesh.proceil.service.dto.ServiceQuotaValueObject
import me.chipnesh.proceil.service.mapper.ServiceQuotaMapper

/**
 * Service for executing complex queries for [ServiceQuotaModel] entities in the database.
 * The main input is a [ServiceQuotaCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [ServiceQuotaValueObject] or a [Page] of [ServiceQuotaValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class ServiceQuotaQueryService(
    val serviceQuotaRepository: ServiceQuotaRepository,
    val serviceQuotaMapper: ServiceQuotaMapper
) : QueryService<ServiceQuotaModel>() {

    private val log = LoggerFactory.getLogger(ServiceQuotaQueryService::class.java)

    /**
     * Return a [MutableList] of [ServiceQuotaValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ServiceQuotaCriteria?): MutableList<ServiceQuotaValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return serviceQuotaMapper.toDto(serviceQuotaRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [ServiceQuotaValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ServiceQuotaCriteria?, page: Pageable): Page<ServiceQuotaValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return serviceQuotaRepository.findAll(specification, page)
            .map(serviceQuotaMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: ServiceQuotaCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return serviceQuotaRepository.count(specification)
    }

    /**
     * Function to convert [ServiceQuotaCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: ServiceQuotaCriteria?): Specification<ServiceQuotaModel?> {
        var specification: Specification<ServiceQuotaModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, ServiceQuotaModel_.id))
            }
            if (criteria.dateFrom != null) {
                specification = specification.and(buildRangeSpecification(criteria.dateFrom, ServiceQuotaModel_.dateFrom))
            }
            if (criteria.dateTo != null) {
                specification = specification.and(buildRangeSpecification(criteria.dateTo, ServiceQuotaModel_.dateTo))
            }
            if (criteria.quotaStatus != null) {
                specification = specification.and(buildSpecification(criteria.quotaStatus, ServiceQuotaModel_.quotaStatus))
            }
            if (criteria.quantityToQuote != null) {
                specification = specification.and(buildRangeSpecification(criteria.quantityToQuote, ServiceQuotaModel_.quantityToQuote))
            }
            if (criteria.serviceId != null) {
                specification = specification.and(buildSpecification(criteria.serviceId) {
                    it.join(ServiceQuotaModel_.service, JoinType.LEFT).get(ServiceModel_.id)
                })
            }
        }
        return specification
    }
}
