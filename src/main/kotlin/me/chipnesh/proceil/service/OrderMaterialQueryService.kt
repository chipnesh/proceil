package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.OrderMaterialModel
import me.chipnesh.proceil.domain.OrderMaterialModel_
import me.chipnesh.proceil.domain.CustomerOrderModel_
import me.chipnesh.proceil.domain.MaterialReserveModel_
import me.chipnesh.proceil.repository.OrderMaterialRepository
import me.chipnesh.proceil.service.dto.OrderMaterialCriteria
import me.chipnesh.proceil.service.dto.OrderMaterialValueObject
import me.chipnesh.proceil.service.mapper.OrderMaterialMapper

/**
 * Service for executing complex queries for [OrderMaterialModel] entities in the database.
 * The main input is a [OrderMaterialCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [OrderMaterialValueObject] or a [Page] of [OrderMaterialValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class OrderMaterialQueryService(
    val orderMaterialRepository: OrderMaterialRepository,
    val orderMaterialMapper: OrderMaterialMapper
) : QueryService<OrderMaterialModel>() {

    private val log = LoggerFactory.getLogger(OrderMaterialQueryService::class.java)

    /**
     * Return a [MutableList] of [OrderMaterialValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: OrderMaterialCriteria?): MutableList<OrderMaterialValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return orderMaterialMapper.toDto(orderMaterialRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [OrderMaterialValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: OrderMaterialCriteria?, page: Pageable): Page<OrderMaterialValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return orderMaterialRepository.findAll(specification, page)
            .map(orderMaterialMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: OrderMaterialCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return orderMaterialRepository.count(specification)
    }

    /**
     * Function to convert [OrderMaterialCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: OrderMaterialCriteria?): Specification<OrderMaterialModel?> {
        var specification: Specification<OrderMaterialModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, OrderMaterialModel_.id))
            }
            if (criteria.materialSummary != null) {
                specification = specification.and(buildStringSpecification(criteria.materialSummary, OrderMaterialModel_.materialSummary))
            }
            if (criteria.createdDate != null) {
                specification = specification.and(buildRangeSpecification(criteria.createdDate, OrderMaterialModel_.createdDate))
            }
            if (criteria.materialQuantity != null) {
                specification = specification.and(buildRangeSpecification(criteria.materialQuantity, OrderMaterialModel_.materialQuantity))
            }
            if (criteria.measureUnit != null) {
                specification = specification.and(buildSpecification(criteria.measureUnit, OrderMaterialModel_.measureUnit))
            }
            if (criteria.reserveId != null) {
                specification = specification.and(buildSpecification(criteria.reserveId) {
                    it.join(OrderMaterialModel_.reserve, JoinType.LEFT).get(MaterialReserveModel_.id)
                })
            }
            if (criteria.orderId != null) {
                specification = specification.and(buildSpecification(criteria.orderId) {
                    it.join(OrderMaterialModel_.order, JoinType.LEFT).get(CustomerOrderModel_.id)
                })
            }
        }
        return specification
    }
}
