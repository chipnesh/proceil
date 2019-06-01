package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.ServiceModel
import me.chipnesh.proceil.repository.ServiceRepository
import me.chipnesh.proceil.service.dto.ServiceValueObject
import me.chipnesh.proceil.service.mapper.ServiceMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [ServiceModel].
 */
@Service
@Transactional
class ServiceService(
    val serviceRepository: ServiceRepository,
    val serviceMapper: ServiceMapper
) {

    private val log = LoggerFactory.getLogger(ServiceService::class.java)

    /**
     * Save a service.
     *
     * @param serviceValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(serviceValueObject: ServiceValueObject): ServiceValueObject {
        log.debug("Request to save Service : {}", serviceValueObject)

        var serviceModel = serviceMapper.toEntity(serviceValueObject)
        serviceModel = serviceRepository.save(serviceModel)
        return serviceMapper.toDto(serviceModel)
    }

    /**
     * Get all the services.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<ServiceValueObject> {
        log.debug("Request to get all Services")
        return serviceRepository.findAll(pageable)
            .map(serviceMapper::toDto)
    }

    /**
     * Get one service by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<ServiceValueObject> {
        log.debug("Request to get Service : {}", id)
        return serviceRepository.findById(id)
            .map(serviceMapper::toDto)
    }

    /**
     * Delete the service by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Service : {}", id)

        serviceRepository.deleteById(id)
    }
}
