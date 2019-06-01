package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.OrderServiceModel
import me.chipnesh.proceil.domain.OrderServiceModel_
import me.chipnesh.proceil.domain.CustomerOrderModel_
import me.chipnesh.proceil.domain.EmployeeModel_
import me.chipnesh.proceil.domain.ServiceQuotaModel_
import me.chipnesh.proceil.repository.OrderServiceRepository
import me.chipnesh.proceil.service.dto.OrderServiceCriteria
import me.chipnesh.proceil.service.dto.OrderServiceValueObject
import me.chipnesh.proceil.service.mapper.OrderServiceMapper

/**
 * Service for executing complex queries for [OrderServiceModel] entities in the database.
 * The main input is a [OrderServiceCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [OrderServiceValueObject] or a [Page] of [OrderServiceValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class OrderServiceQueryService(
    val orderServiceRepository: OrderServiceRepository,
    val orderServiceMapper: OrderServiceMapper
) : QueryService<OrderServiceModel>() {

    private val log = LoggerFactory.getLogger(OrderServiceQueryService::class.java)

    /**
     * Return a [MutableList] of [OrderServiceValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: OrderServiceCriteria?): MutableList<OrderServiceValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return orderServiceMapper.toDto(orderServiceRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [OrderServiceValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: OrderServiceCriteria?, page: Pageable): Page<OrderServiceValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return orderServiceRepository.findAll(specification, page)
            .map(orderServiceMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: OrderServiceCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return orderServiceRepository.count(specification)
    }

    /**
     * Function to convert [OrderServiceCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: OrderServiceCriteria?): Specification<OrderServiceModel?> {
        var specification: Specification<OrderServiceModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, OrderServiceModel_.id))
            }
            if (criteria.serviceSummary != null) {
                specification = specification.and(buildStringSpecification(criteria.serviceSummary, OrderServiceModel_.serviceSummary))
            }
            if (criteria.createdDate != null) {
                specification = specification.and(buildRangeSpecification(criteria.createdDate, OrderServiceModel_.createdDate))
            }
            if (criteria.serviceDate != null) {
                specification = specification.and(buildRangeSpecification(criteria.serviceDate, OrderServiceModel_.serviceDate))
            }
            if (criteria.quotaId != null) {
                specification = specification.and(buildSpecification(criteria.quotaId) {
                    it.join(OrderServiceModel_.quota, JoinType.LEFT).get(ServiceQuotaModel_.id)
                })
            }
            if (criteria.executorId != null) {
                specification = specification.and(buildSpecification(criteria.executorId) {
                    it.join(OrderServiceModel_.executor, JoinType.LEFT).get(EmployeeModel_.id)
                })
            }
            if (criteria.orderId != null) {
                specification = specification.and(buildSpecification(criteria.orderId) {
                    it.join(OrderServiceModel_.order, JoinType.LEFT).get(CustomerOrderModel_.id)
                })
            }
        }
        return specification
    }
}
