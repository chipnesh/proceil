package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.CustomerModel
import me.chipnesh.proceil.domain.CustomerModel_
import me.chipnesh.proceil.domain.CustomerOrderModel_
import me.chipnesh.proceil.domain.FeedbackModel_
import me.chipnesh.proceil.domain.MeasurementModel_
import me.chipnesh.proceil.repository.CustomerRepository
import me.chipnesh.proceil.service.dto.CustomerCriteria
import me.chipnesh.proceil.service.dto.CustomerValueObject
import me.chipnesh.proceil.service.mapper.CustomerMapper

/**
 * Service for executing complex queries for [CustomerModel] entities in the database.
 * The main input is a [CustomerCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [CustomerValueObject] or a [Page] of [CustomerValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class CustomerQueryService(
    val customerRepository: CustomerRepository,
    val customerMapper: CustomerMapper
) : QueryService<CustomerModel>() {

    private val log = LoggerFactory.getLogger(CustomerQueryService::class.java)

    /**
     * Return a [MutableList] of [CustomerValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: CustomerCriteria?): MutableList<CustomerValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return customerMapper.toDto(customerRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [CustomerValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: CustomerCriteria?, page: Pageable): Page<CustomerValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return customerRepository.findAll(specification, page)
            .map(customerMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: CustomerCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return customerRepository.count(specification)
    }

    /**
     * Function to convert [CustomerCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: CustomerCriteria?): Specification<CustomerModel?> {
        var specification: Specification<CustomerModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, CustomerModel_.id))
            }
            if (criteria.customerSummary != null) {
                specification = specification.and(buildStringSpecification(criteria.customerSummary, CustomerModel_.customerSummary))
            }
            if (criteria.firstname != null) {
                specification = specification.and(buildStringSpecification(criteria.firstname, CustomerModel_.firstname))
            }
            if (criteria.lastname != null) {
                specification = specification.and(buildStringSpecification(criteria.lastname, CustomerModel_.lastname))
            }
            if (criteria.middlename != null) {
                specification = specification.and(buildStringSpecification(criteria.middlename, CustomerModel_.middlename))
            }
            if (criteria.birthDate != null) {
                specification = specification.and(buildRangeSpecification(criteria.birthDate, CustomerModel_.birthDate))
            }
            if (criteria.email != null) {
                specification = specification.and(buildStringSpecification(criteria.email, CustomerModel_.email))
            }
            if (criteria.phone != null) {
                specification = specification.and(buildStringSpecification(criteria.phone, CustomerModel_.phone))
            }
            if (criteria.address != null) {
                specification = specification.and(buildStringSpecification(criteria.address, CustomerModel_.address))
            }
            if (criteria.feedbackId != null) {
                specification = specification.and(buildSpecification(criteria.feedbackId) {
                    it.join(CustomerModel_.feedbacks, JoinType.LEFT).get(FeedbackModel_.id)
                })
            }
            if (criteria.measurementId != null) {
                specification = specification.and(buildSpecification(criteria.measurementId) {
                    it.join(CustomerModel_.measurements, JoinType.LEFT).get(MeasurementModel_.id)
                })
            }
            if (criteria.orderId != null) {
                specification = specification.and(buildSpecification(criteria.orderId) {
                    it.join(CustomerModel_.orders, JoinType.LEFT).get(CustomerOrderModel_.id)
                })
            }
        }
        return specification
    }
}
