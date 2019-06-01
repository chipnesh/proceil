package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.CustomerOrderModel
import me.chipnesh.proceil.repository.CustomerOrderRepository
import me.chipnesh.proceil.service.dto.CustomerOrderValueObject
import me.chipnesh.proceil.service.mapper.CustomerOrderMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [CustomerOrderModel].
 */
@Service
@Transactional
class CustomerOrderService(
    val customerOrderRepository: CustomerOrderRepository,
    val customerOrderMapper: CustomerOrderMapper
) {

    private val log = LoggerFactory.getLogger(CustomerOrderService::class.java)

    /**
     * Save a customerOrder.
     *
     * @param customerOrderValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(customerOrderValueObject: CustomerOrderValueObject): CustomerOrderValueObject {
        log.debug("Request to save CustomerOrder : {}", customerOrderValueObject)

        var customerOrderModel = customerOrderMapper.toEntity(customerOrderValueObject)
        customerOrderModel = customerOrderRepository.save(customerOrderModel)
        return customerOrderMapper.toDto(customerOrderModel)
    }

    /**
     * Get all the customerOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<CustomerOrderValueObject> {
        log.debug("Request to get all CustomerOrders")
        return customerOrderRepository.findAll(pageable)
            .map(customerOrderMapper::toDto)
    }

    /**
     * Get one customerOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<CustomerOrderValueObject> {
        log.debug("Request to get CustomerOrder : {}", id)
        return customerOrderRepository.findById(id)
            .map(customerOrderMapper::toDto)
    }

    /**
     * Delete the customerOrder by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete CustomerOrder : {}", id)

        customerOrderRepository.deleteById(id)
    }
}
