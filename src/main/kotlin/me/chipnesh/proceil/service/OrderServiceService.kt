package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.OrderServiceModel
import me.chipnesh.proceil.repository.OrderServiceRepository
import me.chipnesh.proceil.service.dto.OrderServiceValueObject
import me.chipnesh.proceil.service.mapper.OrderServiceMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [OrderServiceModel].
 */
@Service
@Transactional
class OrderServiceService(
    val orderServiceRepository: OrderServiceRepository,
    val orderServiceMapper: OrderServiceMapper
) {

    private val log = LoggerFactory.getLogger(OrderServiceService::class.java)

    /**
     * Save a orderService.
     *
     * @param orderServiceValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(orderServiceValueObject: OrderServiceValueObject): OrderServiceValueObject {
        log.debug("Request to save OrderService : {}", orderServiceValueObject)

        var orderServiceModel = orderServiceMapper.toEntity(orderServiceValueObject)
        orderServiceModel = orderServiceRepository.save(orderServiceModel)
        return orderServiceMapper.toDto(orderServiceModel)
    }

    /**
     * Get all the orderServices.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<OrderServiceValueObject> {
        log.debug("Request to get all OrderServices")
        return orderServiceRepository.findAll(pageable)
            .map(orderServiceMapper::toDto)
    }

    /**
     * Get one orderService by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<OrderServiceValueObject> {
        log.debug("Request to get OrderService : {}", id)
        return orderServiceRepository.findById(id)
            .map(orderServiceMapper::toDto)
    }

    /**
     * Delete the orderService by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete OrderService : {}", id)

        orderServiceRepository.deleteById(id)
    }
}
