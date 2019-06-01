package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.CustomerModel
import me.chipnesh.proceil.repository.CustomerRepository
import me.chipnesh.proceil.service.dto.CustomerValueObject
import me.chipnesh.proceil.service.mapper.CustomerMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [CustomerModel].
 */
@Service
@Transactional
class CustomerService(
    val customerRepository: CustomerRepository,
    val customerMapper: CustomerMapper
) {

    private val log = LoggerFactory.getLogger(CustomerService::class.java)

    /**
     * Save a customer.
     *
     * @param customerValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(customerValueObject: CustomerValueObject): CustomerValueObject {
        log.debug("Request to save Customer : {}", customerValueObject)

        var customerModel = customerMapper.toEntity(customerValueObject)
        customerModel = customerRepository.save(customerModel)
        return customerMapper.toDto(customerModel)
    }

    /**
     * Get all the customers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<CustomerValueObject> {
        log.debug("Request to get all Customers")
        return customerRepository.findAll(pageable)
            .map(customerMapper::toDto)
    }

    /**
     * Get one customer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<CustomerValueObject> {
        log.debug("Request to get Customer : {}", id)
        return customerRepository.findById(id)
            .map(customerMapper::toDto)
    }

    /**
     * Delete the customer by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Customer : {}", id)

        customerRepository.deleteById(id)
    }
}
