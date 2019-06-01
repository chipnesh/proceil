package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.OrderMaterialModel
import me.chipnesh.proceil.repository.OrderMaterialRepository
import me.chipnesh.proceil.service.dto.OrderMaterialValueObject
import me.chipnesh.proceil.service.mapper.OrderMaterialMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [OrderMaterialModel].
 */
@Service
@Transactional
class OrderMaterialService(
    val orderMaterialRepository: OrderMaterialRepository,
    val orderMaterialMapper: OrderMaterialMapper
) {

    private val log = LoggerFactory.getLogger(OrderMaterialService::class.java)

    /**
     * Save a orderMaterial.
     *
     * @param orderMaterialValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(orderMaterialValueObject: OrderMaterialValueObject): OrderMaterialValueObject {
        log.debug("Request to save OrderMaterial : {}", orderMaterialValueObject)

        var orderMaterialModel = orderMaterialMapper.toEntity(orderMaterialValueObject)
        orderMaterialModel = orderMaterialRepository.save(orderMaterialModel)
        return orderMaterialMapper.toDto(orderMaterialModel)
    }

    /**
     * Get all the orderMaterials.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<OrderMaterialValueObject> {
        log.debug("Request to get all OrderMaterials")
        return orderMaterialRepository.findAll(pageable)
            .map(orderMaterialMapper::toDto)
    }

    /**
     * Get one orderMaterial by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<OrderMaterialValueObject> {
        log.debug("Request to get OrderMaterial : {}", id)
        return orderMaterialRepository.findById(id)
            .map(orderMaterialMapper::toDto)
    }

    /**
     * Delete the orderMaterial by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete OrderMaterial : {}", id)

        orderMaterialRepository.deleteById(id)
    }
}
