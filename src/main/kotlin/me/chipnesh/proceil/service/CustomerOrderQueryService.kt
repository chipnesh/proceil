package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.CustomerOrderModel
import me.chipnesh.proceil.domain.CustomerOrderModel_
import me.chipnesh.proceil.domain.CustomerModel_
import me.chipnesh.proceil.domain.EmployeeModel_
import me.chipnesh.proceil.domain.OrderMaterialModel_
import me.chipnesh.proceil.domain.OrderServiceModel_
import me.chipnesh.proceil.repository.CustomerOrderRepository
import me.chipnesh.proceil.service.dto.CustomerOrderCriteria
import me.chipnesh.proceil.service.dto.CustomerOrderValueObject
import me.chipnesh.proceil.service.mapper.CustomerOrderMapper

/**
 * Service for executing complex queries for [CustomerOrderModel] entities in the database.
 * The main input is a [CustomerOrderCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [CustomerOrderValueObject] or a [Page] of [CustomerOrderValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class CustomerOrderQueryService(
    val customerOrderRepository: CustomerOrderRepository,
    val customerOrderMapper: CustomerOrderMapper
) : QueryService<CustomerOrderModel>() {

    private val log = LoggerFactory.getLogger(CustomerOrderQueryService::class.java)

    /**
     * Return a [MutableList] of [CustomerOrderValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: CustomerOrderCriteria?): MutableList<CustomerOrderValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return customerOrderMapper.toDto(customerOrderRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [CustomerOrderValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: CustomerOrderCriteria?, page: Pageable): Page<CustomerOrderValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return customerOrderRepository.findAll(specification, page)
            .map(customerOrderMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: CustomerOrderCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return customerOrderRepository.count(specification)
    }

    /**
     * Function to convert [CustomerOrderCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: CustomerOrderCriteria?): Specification<CustomerOrderModel?> {
        var specification: Specification<CustomerOrderModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, CustomerOrderModel_.id))
            }
            if (criteria.orderSummary != null) {
                specification = specification.and(buildStringSpecification(criteria.orderSummary, CustomerOrderModel_.orderSummary))
            }
            if (criteria.createdDate != null) {
                specification = specification.and(buildRangeSpecification(criteria.createdDate, CustomerOrderModel_.createdDate))
            }
            if (criteria.deadlineDate != null) {
                specification = specification.and(buildRangeSpecification(criteria.deadlineDate, CustomerOrderModel_.deadlineDate))
            }
            if (criteria.orderStatus != null) {
                specification = specification.and(buildSpecification(criteria.orderStatus, CustomerOrderModel_.orderStatus))
            }
            if (criteria.orderPaid != null) {
                specification = specification.and(buildSpecification(criteria.orderPaid, CustomerOrderModel_.orderPaid))
            }
            if (criteria.materialsId != null) {
                specification = specification.and(buildSpecification(criteria.materialsId) {
                    it.join(CustomerOrderModel_.materials, JoinType.LEFT).get(OrderMaterialModel_.id)
                })
            }
            if (criteria.serviceId != null) {
                specification = specification.and(buildSpecification(criteria.serviceId) {
                    it.join(CustomerOrderModel_.services, JoinType.LEFT).get(OrderServiceModel_.id)
                })
            }
            if (criteria.managerId != null) {
                specification = specification.and(buildSpecification(criteria.managerId) {
                    it.join(CustomerOrderModel_.manager, JoinType.LEFT).get(EmployeeModel_.id)
                })
            }
            if (criteria.customerId != null) {
                specification = specification.and(buildSpecification(criteria.customerId) {
                    it.join(CustomerOrderModel_.customer, JoinType.LEFT).get(CustomerModel_.id)
                })
            }
        }
        return specification
    }
}
